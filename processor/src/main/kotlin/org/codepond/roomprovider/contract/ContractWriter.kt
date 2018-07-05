package org.codepond.roomprovider.contract

import com.google.common.base.CaseFormat
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.ext.AndroidTypeNames
import org.codepond.roomprovider.ext.getPackage
import javax.lang.model.element.Modifier

class ContractWriter(private val database: Database,
                     private val context: Context) {
    private val publicStaticFinalModifier = arrayOf(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
    fun write(): Contract {
        val contentAuthority = FieldSpec.builder(String::class.java, "CONTENT_AUTHORITY", *publicStaticFinalModifier)
                .initializer("\$S", database.element.getPackage(context))
                .build()

        val baseContentUri = FieldSpec.builder(AndroidTypeNames.URI, "BASE_CONTENT_URI", *publicStaticFinalModifier)
                .initializer("new Uri.Builder().scheme(\"content\").encodedAuthority(\$N).build()", contentAuthority)
                .build()

        val contractName = "${database.name.capitalize()}Contract"
        val contract = TypeSpec.classBuilder(contractName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(contentAuthority)
                .addField(baseContentUri)

        val contents = mutableSetOf<Content>()
        contract.apply {
            database.entities.forEach {
                addType(generateEntityContractClass(it, contents))
            }
        }

        JavaFile.builder(database.element.getPackage(context), contract.build()).build().writeTo(context.processingEnv.filer)

        return Contract(contractName, contents)
    }

    private fun generateEntityContractClass(entity: Entity, contents: MutableSet<Content>): TypeSpec {
        val contentName = entity.name.capitalize()
        val columns = mutableSetOf<String>()

        return TypeSpec.classBuilder(contentName).apply {
            addModifiers(*publicStaticFinalModifier)

            addField(FieldSpec.builder(AndroidTypeNames.URI, "CONTENT_URI", *publicStaticFinalModifier)
                    .initializer("BASE_CONTENT_URI.buildUpon().appendPath(\$S).build()", entity.name)
                    .build())

            addField(FieldSpec.builder(String::class.java, "CONTENT_TYPE", *publicStaticFinalModifier)
                    .initializer("\$T.CURSOR_DIR_BASE_TYPE + \"/vnd.\" + CONTENT_AUTHORITY + \".\$L\"", AndroidTypeNames.CONTENT_RESOLVER, entity.name)
                    .build())

            addField(FieldSpec.builder(String::class.java, "CONTENT_ITEM_TYPE", *publicStaticFinalModifier)
                    .initializer("\$T.CURSOR_ITEM_BASE_TYPE + \"/vnd.\" + CONTENT_AUTHORITY + \".\$L\"", AndroidTypeNames.CONTENT_RESOLVER, entity.name)
                    .build())

            entity.columns.forEach {
                val fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, it)
                columns.add(fieldName)
                addField(FieldSpec.builder(String::class.java, fieldName, *publicStaticFinalModifier)
                        .initializer("\$S", it)
                        .build())
            }

            contents.add(Content(contentName, columns))
        }.build()
    }
}
