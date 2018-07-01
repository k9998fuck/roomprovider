package org.codepond.roomprovider.contract

import android.arch.persistence.room.Database
import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.MoreElements
import com.google.auto.common.MoreTypes
import org.codepond.roomprovider.Context
import org.codepond.roomprovider.ContextAwareProcessor
import org.codepond.roomprovider.ext.toListOfClassTypes
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

class DatabaseProcessor(context: Context,
                        element: TypeElement
) : ContextAwareProcessor(context, element) {
    fun process(): org.codepond.roomprovider.contract.Database {
        val dbAnnotation = MoreElements
                .getAnnotationMirror(element, Database::class.java)
                .orNull()
        val entities = processEntities(dbAnnotation)
        val databaseName = processDatabaseName()

        if (databaseName.isNullOrEmpty()) {
            context.logger.e(element, "Could not resolve database name. Did you annotate an anonymous class?")
        }

        return Database(element, databaseName!!, entities)
    }

    private fun processEntities(dbAnnotation: AnnotationMirror?): List<Entity> {
        val entityList = AnnotationMirrors.getAnnotationValue(dbAnnotation, "entities")
        val listOfTypes = entityList.toListOfClassTypes()
        return listOfTypes.map {
            EntityProcessor(context, MoreTypes.asTypeElement(it)).process()
        }
    }

    private fun processDatabaseName(): String? {
        return element.simpleName?.toString()
                ?.replace(Regex("(Db|Database)"), "")
                ?.capitalize()
    }
}
