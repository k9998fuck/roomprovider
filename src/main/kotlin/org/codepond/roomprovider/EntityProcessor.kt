package org.codepond.roomprovider

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.MoreElements
import org.codepond.roomprovider.ext.getAllFieldsIncludingPrivateSupers
import org.codepond.roomprovider.ext.hasAnnotation
import org.codepond.roomprovider.ext.hasAnyOf
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.Modifier.TRANSIENT

class EntityProcessor(context: Context,
                      entityElement: TypeElement
) : ContextAwareProcessor(context, entityElement) {
    fun process(): org.codepond.roomprovider.Entity {
        context.logger.d(element, "Found entity")
        val annotation = MoreElements.getAnnotationMirror(element, Entity::class.java).orNull()
        val tableName = extractAnnotationValue(element, annotation, "tableName")

        return Entity(element, tableName, getColumns())
    }

    private fun getColumns(): Set<String> {
        val allFields = element.getAllFieldsIncludingPrivateSupers(context.processingEnv)
                .filter {
                    !it.hasAnnotation(Ignore::class)
                            && !it.hasAnyOf(STATIC)
                            && (!it.hasAnyOf(TRANSIENT) || it.hasAnnotation(ColumnInfo::class))
                }

        val columns = mutableSetOf<String>()
        for (field in allFields) {
            val annotation = MoreElements.getAnnotationMirror(field, ColumnInfo::class.java).orNull()

            val columnName = if (annotation != null)
                extractAnnotationValue(field, annotation, "name", listOf(ColumnInfo.INHERIT_FIELD_NAME))
            else field.simpleName.toString()

            context.logger.d(field, "Adding field")
            columns.add(columnName)
        }
        if (columns.size == 0) {
            context.logger.e(element, "Entity class ${element.qualifiedName} does not contains any fields")
        }
        return columns
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
