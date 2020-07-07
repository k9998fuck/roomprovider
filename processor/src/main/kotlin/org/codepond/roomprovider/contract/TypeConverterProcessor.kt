package org.codepond.roomprovider.contract

import androidx.room.ColumnInfo
import androidx.room.Ignore
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
import javax.lang.model.util.ElementFilter

class TypeConverterProcessor(context: Context,
                             entityElement: TypeElement
) : ContextAwareProcessor(context, entityElement) {
    fun process(): TypeConverter {
        context.logger.d(element, "Found TypeConverter")
        return TypeConverter(element, element.simpleName.toString(), getMethods())
    }

    private fun getMethods(): Set<Method> {
        val allMethods = ElementFilter.methodsIn(element.enclosedElements)
                .filter {
                    !it.hasAnnotation(Ignore::class) && it.hasAnnotation(androidx.room.TypeConverter::class)
                }

        val methods = mutableSetOf<Method>()
        for (method in allMethods) {
            if(method.parameters.size==1){
                context.logger.d(method, "Adding method")
                methods.add(Method(method, method.simpleName.toString(),
                        TypeName.get(method.parameters[0].asType()),
                        TypeName.get(method.returnType)))
            }
        }
        if (methods.size == 0) {
            context.logger.e(element, "Entity class ${element.qualifiedName} does not contains any methods")
        }
        return methods
    }
}
