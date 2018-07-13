package org.codepond.roomprovider.contract

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.ext.AndroidTypeNames
import org.codepond.roomprovider.ext.getPackage
import java.io.File

class ExtensionWriter(private val database: Database,
                      private val context: Context) {
    fun write() {
        val kaptKotlinGeneratedDir = context.processingEnv.options["kapt.kotlin.generated"]
        val pack = database.element.getPackage(context)
        database.entities.forEach {
            createExtensionFile(it, pack, kaptKotlinGeneratedDir)
        }
    }

    private fun createExtensionFile(entity: Entity, pack: String, kaptKotlinGeneratedDir: String?) {
        val name = entity.element.simpleName.toString()
        FileSpec.builder(pack, name + "Ext")
                .addFunction(toContentValues(pack, name, entity))
                .build()
                .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun toContentValues(pack: String, name: String, entity: Entity): FunSpec {
        return FunSpec.builder("toContentValues").apply {
            receiver(ClassName(pack, name))
            addStatement("val values = %T()", AndroidTypeNames.CONTENT_VALUES)
            entity.fields.forEach { field ->
                checkSupportedType(field)
                addStatement("values.put(\"${field.name}\", ${field.name})")
            }
            addStatement("return values")
            returns(AndroidTypeNames.CONTENT_VALUES)
        }.build()
    }

    private fun checkSupportedType(field: Field) {

        if (!field.type.isPrimitive && !field.type.isBoxedPrimitive && field.type != com.squareup.javapoet.ClassName.get("java.lang", "String")) {
            context.logger.e(field.element, "${field.name} is not supported field type")
        }
    }
}
