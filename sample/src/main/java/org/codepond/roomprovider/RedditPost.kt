package org.codepond.roomprovider

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "posts")
data class RedditPost(
        @PrimaryKey
        val name: String,
        val title: String,
        val score: Int,
        val author: String,
        @ColumnInfo(collate = ColumnInfo.NOCASE)
        val num_comments: Int,
        val created: Long,
        val thumbnail: String?,
        val url: String?)
