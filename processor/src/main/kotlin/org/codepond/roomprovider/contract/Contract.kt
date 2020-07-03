package org.codepond.roomprovider.contract

data class Contract(val name: String,
                    val contents: Set<Content>){

    override fun toString(): String {
        return String.format("{name=%s contents=%s}",
                name,
                contents.let {
                    var value = "["
                    it.forEach {
                        value += "$it,"
                    }
                    value += "]"
                    value
                })
    }
}

