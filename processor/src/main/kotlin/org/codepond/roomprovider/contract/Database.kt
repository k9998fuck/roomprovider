package org.codepond.roomprovider.contract

import javax.lang.model.element.TypeElement

data class Database(val element: TypeElement,
                    val name: String,
                    val entities: List<Entity>,
                    val typeConverters: List<TypeConverter>){

    override fun toString(): String {
        return String.format("{element=%s name=%s entities=%s typeConverters=%s}",
                element.toString(),
                name,
                entities.let {
                    var value = "["
                    it.forEach {
                        value += "$it,"
                    }
                    value += "]"
                    value
                },
                typeConverters.let {
                    var value = "["
                    it.forEach {
                        value += "$it,"
                    }
                    value += "]"
                    value
                })
    }
}
