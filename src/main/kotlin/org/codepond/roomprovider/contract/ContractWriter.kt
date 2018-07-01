package org.codepond.roomprovider.contract

import com.google.common.base.CaseFormat
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.Database
import org.codepond.roomprovider.Entity
import org.codepond.roomprovider.ext.AndroidTypeNames
import javax.lang.model.element.Modifier

class ContractWriter(private val database: Database,
                     private val context: Context) {
    private val publicStaticFinalModifier = arrayOf(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
    fun write() {
        val contentAuthority = FieldSpec.builder(String::class.java, "CONTENT_AUTHORITY", *publicStaticFinalModifier)
                .initializer("\$S", getDatabasePackage())
                .build()

        val baseContentUri = FieldSpec.builder(AndroidTypeNames.URI, "BASE_CONTENT_URI", *publicStaticFinalModifier)
                .initializer("Uri.parse(\"content://\" + CONTENT_AUTHORITY)")
                .build()

        val contract = TypeSpec.classBuilder("${database.name.capitalize()}Contract")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(contentAuthority)
                .addField(baseContentUri)

        contract.apply {
            database.entities.forEach {
                addType(generateEntityContractClass(it))
            }
        }

        JavaFile.builder(getDatabasePackage(), contract.build()).build().writeTo(context.processingEnv.filer)
    }

    private fun getDatabasePackage() = context.processingEnv.elementUtils.getPackageOf(database.element).qualifiedName.toString()

    private fun generateEntityContractClass(entity: Entity): TypeSpec {
        return TypeSpec.classBuilder(entity.name.capitalize()).apply {
            addModifiers(*publicStaticFinalModifier)

            addField(FieldSpec.builder(AndroidTypeNames.URI, "CONTENT_URI", *publicStaticFinalModifier)
                    .initializer("BASE_CONTENT_URI.buildUpon().appendPath(\$S).build()", entity.name)
                    .build())

            addField(FieldSpec.builder(String::class.java, "CONTENT_TYPE", *publicStaticFinalModifier)
                    .initializer("\$T.CURSOR_DIR_BASE_TYPE + \"/vnd.\" + CONTENT_AUTHORITY + \$S", AndroidTypeNames.CONTENT_RESOLVER, ".${entity.name}")
                    .build())

            addField(FieldSpec.builder(String::class.java, "CONTENT_ITEM_TYPE", *publicStaticFinalModifier)
                    .initializer("\$T.CURSOR_ITEM_BASE_TYPE + \"/vnd.\" + CONTENT_AUTHORITY + \$S", AndroidTypeNames.CONTENT_RESOLVER, ".${entity.name}")
                    .build())

            entity.columns.forEach {
                val fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, it)
                addField(FieldSpec.builder(String::class.java, fieldName, *publicStaticFinalModifier)
                        .initializer("\$S", it)
                        .build())
            }


        }.build()
    }
}
