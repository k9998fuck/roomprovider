package org.codepond.roomprovider.contract

import javax.lang.model.element.TypeElement

data class TypeConverter(val element: TypeElement,
                         val name: String,
                         val methods: Set<Method>)
