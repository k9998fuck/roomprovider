package org.codepond.roomprovider.provider

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.contract.Contract
import org.codepond.roomprovider.contract.Database
import org.codepond.roomprovider.ext.AndroidTypeNames
import org.codepond.roomprovider.ext.getPackage
import javax.lang.model.element.Modifier
import javax.lang.model.util.ElementFilter

class ContentProviderWriter(private val database: Database,
                            private val contract: Contract,
                            private val context: Context) {


    fun write() {
        for(entity in database.entities){

            val provider = TypeSpec.classBuilder("${entity.name.capitalize()}ContentProvider").apply {
                addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                superclass(AndroidTypeNames.CONTENT_PROVIDER)
//                addMethod(MethodSpec.methodBuilder("onCreate")
//                        .addAnnotation(Override::class.java)
//                        .addModifiers(Modifier.PUBLIC)
//                        .returns(TypeName.BOOLEAN)
//                        .addStatement("return true")
//                        .build())
                val classElement = context.processingEnv.elementUtils.getTypeElement(AndroidTypeNames.CONTENT_PROVIDER.canonicalName())
                val methods = ElementFilter.methodsIn(classElement.enclosedElements)
                val databaseProvider = ClassName.get(database.element.getPackage(context), "${database.name.capitalize()}Provider")
                val databaseContract = ClassName.get(database.element.getPackage(context), "${database.name.capitalize()}Contract")
                methods.forEach {
                    when(it.simpleName.toString()){
                        "onCreate" -> addMethod(MethodSpec.overriding(it).addStatement("return true").build())
                        "query" -> {
                            if(it.modifiers.contains(Modifier.ABSTRACT)){
                                addMethod(MethodSpec.overriding(it)
                                        .addStatement("\$T localSelection",ClassName.get("java.lang", "String"))
                                        .addCode("switch (\$T.MATCHER.match(arg0)){\n",databaseProvider)
                                        .addCode("case \$T.${entity.name.toUpperCase()}_ITEM:\n",databaseProvider)
                                        .apply {
                                            var add = false
                                            for (field in entity.fields){
                                                if(field.primaryKey){
                                                    addCode("localSelection = (\"${field.name} = \" + arg0.getLastPathSegment()) + (arg2==null || arg2.trim().isEmpty() ? \"\" : \" AND \"+arg2);\n",AndroidTypeNames.BASE_COLUMNS)
                                                    add = true
                                                    break
                                                }
                                            }
                                            if(!add){
                                                addCode("localSelection = (\$T._ID + \" = \" + arg0.getLastPathSegment()) + (arg2==null || arg2.trim().isEmpty() ? \"\" : \" AND \"+arg2);\n",AndroidTypeNames.BASE_COLUMNS)
                                            }
                                        }
                                        .addCode("break;\n")
                                        .addCode("default:\n")
                                        .addCode("localSelection = arg2;\n")
                                        .addCode("break;\n")
                                        .addCode("}\n")
                                        .addStatement("\$T queryBuilder = \$T.builder(\$S)",
                                                AndroidTypeNames.SUPPORT_SQLITE_QUERY_BUILDER,
                                                AndroidTypeNames.SUPPORT_SQLITE_QUERY_BUILDER,
                                                entity.name)
                                        .addStatement("queryBuilder.columns(arg1)")
                                        .addStatement("queryBuilder.selection(localSelection,arg3)")
                                        .addStatement("queryBuilder.orderBy(arg4)")
                                        .addStatement("\$T query = queryBuilder.create()",AndroidTypeNames.SUPPORT_SQLITE_QUERY)
                                        .addStatement("\$T c = \$T.Companion.getDb().query(query)",
                                                AndroidTypeNames.CURSOR,ClassName.get(database.element.getPackage(context), "${database.element.simpleName}"))
                                        .addStatement("return c").build())
                            }
                        }
                        "getType" -> {
                            addMethod(MethodSpec.overriding(it)
                                    .addStatement("\$T value",ClassName.get("java.lang", "String"))
                                    .addCode("switch (\$T.MATCHER.match(arg0)){\n",databaseProvider)
                                    .addCode("case \$T.${entity.name.toUpperCase()}_ITEM:\n",databaseProvider)
                                    .addCode("value = \$T.${entity.name.capitalize()}.CONTENT_ITEM_TYPE;\n",databaseContract)
                                    .addCode("break;\n")
                                    .addCode("default:\n")
                                    .addCode("value = \$T.${entity.name.capitalize()}.CONTENT_TYPE;\n",databaseContract)
                                    .addCode("break;\n")
                                    .addCode("}\n")
                                    .addStatement("return value").build())
                        }
                        "insert" -> addMethod(MethodSpec.overriding(it).addStatement("return null").build())
                        "delete" -> addMethod(MethodSpec.overriding(it).addStatement("return 0").build())
                        "update" -> addMethod(MethodSpec.overriding(it).addStatement("return 0").build())
                    }
                }



            }

            JavaFile.builder(entity.element.getPackage(context), provider.build()).build().writeTo(context.processingEnv.filer)
        }




    }
}
