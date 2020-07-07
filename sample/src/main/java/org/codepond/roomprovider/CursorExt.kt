package org.codepond.roomprovider

import android.database.Cursor

fun Cursor.format(): String {
    var value = "Cursor["
    for(i in 0 until columnCount){
        when(getType(i)){
            Cursor.FIELD_TYPE_INTEGER -> getInt(i).toString()
            Cursor.FIELD_TYPE_FLOAT -> getFloat(i).toString()
            Cursor.FIELD_TYPE_STRING -> getString(i).toString()
            Cursor.FIELD_TYPE_BLOB -> getBlob(i).toString()
            else -> null
        }?.also {
            value += " $it"
        }
    }
    value += "]"
    return value
}