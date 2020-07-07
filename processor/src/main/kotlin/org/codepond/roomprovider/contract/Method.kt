package org.codepond.roomprovider.contract

import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class Method(val element: ExecutableElement,
                  val name: String,
                  val parameterTypeName: TypeName,
                  val returnTypeName: TypeName
                  )
