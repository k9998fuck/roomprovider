package org.codepond.roomprovider.contract

import com.squareup.javapoet.TypeName
import javax.lang.model.element.VariableElement

data class Field(val element: VariableElement,
                 val name: String,
                 val type: TypeName,
                 val primaryKey: Boolean)
