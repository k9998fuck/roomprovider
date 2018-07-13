package org.codepond.roomprovider.contract

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Ignore
import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.MoreElements
import com.google.auto.common.MoreTypes
import com.squareup.javapoet.TypeName
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.ContextAwareProcessor
import org.codepond.roomprovider.ext.getAllFieldsIncludingPrivateSupers
import org.codepond.roomprovider.ext.hasAnnotation
import org.codepond.roomprovider.ext.hasAnyOf
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.Modifier.TRANSIENT
import javax.lang.model.element.TypeElement

class EntityProcessor(context: Context,
                      entityElement: TypeElement
) : ContextAwareProcessor(context, entityElement) {
    fun process(): Entity {
        context.logger.d(element, "Found entity")
        val annotation = MoreElements
                .getAnnotationMirror(element, android.arch.persistence.room.Entity::class.java)
                .orNull()
        val tableName = extractAnnotationValue(element, annotation, "tableName")

        return Entity(element, tableName, getFields())
    }

    private fun getFields(): Set<Field> {
        val allFields = element.getAllFieldsIncludingPrivateSupers(context.processingEnv)
                .filter {
                    !it.hasAnnotation(Ignore::class)
                            && !it.hasAnyOf(STATIC)
                            && (!it.hasAnyOf(TRANSIENT) || it.hasAnnotation(ColumnInfo::class))
                }

        val fields = mutableSetOf<Field>()
        for (field in allFields) {
            val annotation = MoreElements.getAnnotationMirror(field, ColumnInfo::class.java).orNull()

            val fieldName = if (annotation != null)
                extractAnnotationValue(field, annotation, "name", listOf(ColumnInfo.INHERIT_FIELD_NAME))
            else field.simpleName.toString()

            context.logger.d(field, "Adding field")
            val declaredType = MoreTypes.asDeclared(element.asType())
            val member = context.processingEnv.typeUtils.asMemberOf(declaredType, field)
            fields.add(Field(field, fieldName, TypeName.get(member)))
        }
        if (fields.size == 0) {
            context.logger.e(element, "Entity class ${element.qualifiedName} does not contains any fields")
        }
        return fields
    }

    private fun extractAnnotationValue(element: Element, annotation: AnnotationMirror?, elementName: String, defaults: List<String> = emptyList()): String {
        val annotationValue = AnnotationMirrors.getAnnotationValue(annotation, elementName).value.toString()
        return if (annotationValue.isEmpty() || defaults.contains(annotationValue)) {
            element.simpleName.toString()
        } else {
            annotationValue
        }
    }
}
