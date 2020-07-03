package org.codepond.roomprovider.provider

import com.squareup.javapoet.*
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.contract.Content
import org.codepond.roomprovider.contract.Contract
import org.codepond.roomprovider.contract.Database
import org.codepond.roomprovider.ext.AndroidTypeNames
import org.codepond.roomprovider.ext.getPackage
import org.codepond.roomprovider.ext.publicStaticFinalModifier
import org.codepond.roomprovider.ext.publicStaticFinalModifier
import javax.lang.model.element.Modifier

class ProviderWriter(private val database: Database,
                     private val contract: Contract,
                     private val context: Context) {
    fun write() {
        context.logger.w(database.element,String.format("ProviderWriter database=%s",database.toString()))
        context.logger.w(database.element,String.format("ProviderWriter contract=%s",contract.toString()))
        val provider = TypeSpec.classBuilder("${database.name.capitalize()}Provider").apply {
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)

            var i = 0
            val dirCodes = mutableMapOf<Content, String>()
            val itemCodes = mutableMapOf<Content, String>()
            contract.contents.forEach {
                val name = it.name.toUpperCase()
                val dir = "${name}_DIR"
                dirCodes[it] = dir
                addField(FieldSpec.builder(TypeName.INT, dir, *publicStaticFinalModifier)
                        .initializer("\$L", ++i)
                        .build())

                val item = "${name}_ITEM"
                itemCodes[it] = item
                addField(FieldSpec.builder(TypeName.INT, item, *publicStaticFinalModifier)
                        .initializer("\$L", ++i)
                        .build())
            }

            addField(FieldSpec.builder(AndroidTypeNames.URI_MATCHER, "MATCHER").apply {
                addModifiers(*publicStaticFinalModifier)
                initializer("new \$T(UriMatcher.NO_MATCH)", AndroidTypeNames.URI_MATCHER)
            }.build())
            addStaticBlock(CodeBlock.builder().apply {
                contract.contents.forEach {
                    val contentName = it.name.toLowerCase()
                    addStatement("MATCHER.addURI(${contract.name}.CONTENT_AUTHORITY, \$S, ${dirCodes[it]})", contentName)
                    addStatement("MATCHER.addURI(${contract.name}.CONTENT_AUTHORITY, \"\$L/*\", ${itemCodes[it]})", contentName)
                }
            }.build())

        }

        JavaFile.builder(database.element.getPackage(context), provider.build()).build().writeTo(context.processingEnv.filer)
    }
}
