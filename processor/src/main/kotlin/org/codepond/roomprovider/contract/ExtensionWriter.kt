package org.codepond.roomprovider.contract

import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.*
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.ext.AndroidTypeNames
import org.codepond.roomprovider.ext.getPackage
import org.codepond.roomprovider.ext.isNonNull
import org.codepond.roomprovider.ext.toClassName
import java.io.File
import javax.lang.model.element.Modifier

class ExtensionWriter(private val database: Database,
                      private val context: Context) {
    fun write() {
        val kaptKotlinGeneratedDir = context.processingEnv.options["kapt.kotlin.generated"]
        database.entities.forEach {
            createExtensionFile(it, kaptKotlinGeneratedDir)
        }
    }

    private fun createExtensionFile(entity: Entity, kaptKotlinGeneratedDir: String?) {
        val name = entity.element.simpleName.toString()
        val pack = entity.element.getPackage(context)
        FileSpec.builder(pack, name + "Ext")
                .addFunction(toContentValues(pack, name, entity))
                .addFunction(fromContentValues(pack, name, entity))
                .addFunction(fromCursor(pack, name, entity))
                .build()
                .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun toContentValues(pack: String, name: String, entity: Entity): FunSpec {
        return FunSpec.builder("toContentValues").apply {
            receiver(ClassName(pack, name))
            addStatement("val values = %T()", AndroidTypeNames.CONTENT_VALUES.toClassName())
            entity.fields.forEach { field ->
                checkSupportedType(field)
                if (!field.type.isPrimitive && !field.type.isBoxedPrimitive && field.type != com.squareup.javapoet.ClassName.get("java.lang", "String")) {
                    for(typeConverter in database.typeConverters){
                        for(method in typeConverter.methods){
                            if(method.parameterTypeName == field.type){
                                addStatement("values.put(\"${field.name}\", %T().${method.element
                                        .simpleName}(${field.element}))",typeConverter.element.asType().asTypeName())
                                break
                            }
                        }
                    }
                }else{
                    addStatement("values.put(\"${field.name}\", ${field.element})")
                }
            }
            addStatement("return values")
            returns(AndroidTypeNames.CONTENT_VALUES.toClassName())
        }.build()
    }

    private fun fromContentValues(pack: String, name: String, entity: Entity): FunSpec {
        return FunSpec.builder("to${name}").apply {
            receiver(AndroidTypeNames.CONTENT_VALUES.toClassName())
            for((index,typeConverter) in database.typeConverters.withIndex()){
                addStatement("val typeConverter${index} = %T()",typeConverter.element.asType().asTypeName())
            }
            var content = "return %T("
            entity.fields.forEachIndexed { index, field ->
                checkSupportedType(field)
                content += if(field.type.isPrimitive || field.type.isBoxedPrimitive){
                    when(field.type.unbox()){
                        TypeName.BOOLEAN -> "${field.element} = getAsBoolean(\"${field.name}\")"
                        TypeName.BYTE -> "${field.element} = getAsByte(\"${field.name}\")"
                        TypeName.SHORT -> "${field.element} = getAsShort(\"${field.name}\")"
                        TypeName.INT -> "${field.element} = getAsInteger(\"${field.name}\")"
                        TypeName.LONG -> "${field.element} = getAsLong(\"${field.name}\")"
                        TypeName.FLOAT -> "${field.element} = getAsFloat(\"${field.name}\")"
                        TypeName.DOUBLE -> "${field.element} = getAsDouble(\"${field.name}\")"
                        else -> "null"
                    }
                }else{
                    when(field.type){
                        com.squareup.javapoet.ClassName.get("java.lang", "String") -> "${field.element} = getAsString(\"${field.name}\")"
                        TypeName.OBJECT -> "${field.element} = get(\"${field.name}\")"
                        else -> {
                            var code = ""
                            for((index,typeConverter) in database.typeConverters.withIndex()){
                                for(method in typeConverter.methods){
                                    if(method.returnTypeName == field.type){
                                        code = CodeBlock.builder().add("${field.element} = typeConverter${index}" +
                                                ".${method.element.simpleName}(" +
                                                "${if(method.parameterTypeName.isPrimitive || method.parameterTypeName.isBoxedPrimitive){
                                                    when(method.parameterTypeName.unbox()){
                                                        TypeName.BOOLEAN -> "getAsBoolean"
                                                        TypeName.BYTE -> "getAsByte"
                                                        TypeName.SHORT -> "getAsShort"
                                                        TypeName.INT -> "getAsInteger"
                                                        TypeName.LONG -> "getAsLong"
                                                        TypeName.FLOAT -> "getAsFloat"
                                                        TypeName.DOUBLE -> "getAsDouble"
                                                        else -> "null"
                                                    }
                                                }else{
                                                    when(method.parameterTypeName){
                                                        com.squareup.javapoet.ClassName.get("java.lang", "String") -> "getAsString"
                                                        else -> "null"
                                                    }
                                                }}(\"${field.name}\"))${if(field.element.isNonNull()) "!!" else ""}"
                                        ).build().toString()
                                        break
                                    }
                                }
                            }
                            code
                        }
                    }
                } + if(index!=entity.fields.size-1) "," else ""
            }
            content += ")"
            addStatement(content, entity.element.asType().asTypeName())
            returns(entity.element.asType().asTypeName())

        }.build()
    }

    private fun fromCursor(pack: String, name: String, entity: Entity): FunSpec {
        return FunSpec.builder("to${name}").apply {
            receiver(AndroidTypeNames.CURSOR.toClassName())
            for((index,typeConverter) in database.typeConverters.withIndex()){
                addStatement("val typeConverter${index} = %T()",typeConverter.element.asType().asTypeName())
            }
            var content = "return %T("
            entity.fields.forEachIndexed { index, field ->
                checkSupportedType(field)
                content += if(field.type.isPrimitive || field.type.isBoxedPrimitive){
                    when(field.type.unbox()){
                        TypeName.BOOLEAN -> "${field.element} = 1L==getLong(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.BYTE -> "${field.element} = getByte(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.SHORT -> "${field.element} = getShort(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.INT -> "${field.element} = getInt(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.LONG -> "${field.element} = getLong(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.FLOAT -> "${field.element} = getFloat(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.DOUBLE -> "${field.element} = getDouble(getColumnIndexOrThrow(\"${field.name}\"))"
                        else -> "null"
                    }
                }else{
                    when(field.type){
                        com.squareup.javapoet.ClassName.get("java.lang", "String") -> "${field.element} = getString(getColumnIndexOrThrow(\"${field.name}\"))"
                        TypeName.OBJECT -> "${field.element} = get(\"${field.name}\")"
                        else -> {
                            var code = ""
                            for((index,typeConverter) in database.typeConverters.withIndex()){
                                for(method in typeConverter.methods){
                                    if(method.returnTypeName == field.type){
                                        code = CodeBlock.builder().add("${field.element} = typeConverter${index}" +
                                                ".${method.element.simpleName}(" +
                                                "${if(method.parameterTypeName.isPrimitive || method.parameterTypeName.isBoxedPrimitive){
                                                    when(method.parameterTypeName.unbox()){
                                                        TypeName.BOOLEAN -> "1L==getLong(getColumnIndexOrThrow"
                                                        TypeName.BYTE -> "getByte(getColumnIndexOrThrow"
                                                        TypeName.SHORT -> "getShort(getColumnIndexOrThrow"
                                                        TypeName.INT -> "getInt(getColumnIndexOrThrow"
                                                        TypeName.LONG -> "getLong(getColumnIndexOrThrow"
                                                        TypeName.FLOAT -> "getFloat(getColumnIndexOrThrow"
                                                        TypeName.DOUBLE -> "getDouble(getColumnIndexOrThrow"
                                                        else -> "null"
                                                    }
                                                }else{
                                                    when(method.parameterTypeName){
                                                        com.squareup.javapoet.ClassName.get("java.lang", "String") -> "getString(getColumnIndexOrThrow"
                                                        else -> "null"
                                                    }
                                                }}(\"${field.name}\")))${if(field.element.isNonNull()) "!!" else ""}"
                                        ).build().toString()
                                        break
                                    }
                                }
                            }
                            code
                        }
                    }
                } + if(index!=entity.fields.size-1) "," else ""
            }
            content += ")"
            addStatement(content, entity.element.asType().asTypeName())
            returns(entity.element.asType().asTypeName())

        }.build()
    }

    private fun checkSupportedType(field: Field) {
        if (!field.type.isPrimitive && !field.type.isBoxedPrimitive && field.type != com.squareup.javapoet.ClassName.get("java.lang", "String")) {
            var supported = false
            for(typeConverter in database.typeConverters){
                for(method in typeConverter.methods){
                    if(method.parameterTypeName == field.type){
                        context.logger.d(field.element, "${field.name} is supported field type ${method.parameterTypeName}")
                        supported = true
                        break
                    }
                }
            }
            if(!supported){
                context.logger.e(field.element, "${field.name} is not supported field type")
            }
        }
    }

}
