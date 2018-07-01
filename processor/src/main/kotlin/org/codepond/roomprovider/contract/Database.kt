package org.codepond.roomprovider.contract

import javax.lang.model.element.TypeElement

data class Database(val element: TypeElement,
                    val name: String,
                    val entities: List<Entity>)
