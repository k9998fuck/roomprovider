package org.codepond.roomprovider.contract

import javax.lang.model.element.TypeElement

data class Database(val element: TypeElement,
                    val name: String,
                    val entities: List<Entity>){

    override fun toString(): String {
        return String.format("{element=%s name=%s entities=%s}",
                element.toString(),
                name,
                entities.let {
                    var value = "["
                    it.forEach {
                        value += "$it,"
                    }
                    value += "]"
                    value
                })
    }
}
