package org.codepond.roomprovider.converter

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { simpleDateFormat.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.let { simpleDateFormat.format(it) }
    }

}