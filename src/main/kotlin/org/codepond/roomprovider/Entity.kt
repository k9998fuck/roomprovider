package org.codepond.roomprovider

import javax.lang.model.element.TypeElement

data class Entity(val element: TypeElement,
                  val name: String,
                  val columns: Set<String>)
