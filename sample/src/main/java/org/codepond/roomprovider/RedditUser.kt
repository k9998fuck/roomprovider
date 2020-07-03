package org.codepond.roomprovider

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.provider.BaseColumns

@Entity(tableName = "users")
data class RedditUser(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(
                index = true,
                name = BaseColumns._ID
        )
        val id: Long,
        val username: String,
        val password: String)
