package org.codepond.roomprovider.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import android.database.Cursor
import org.codepond.roomprovider.bean.RedditPost

@Dao
interface RedditPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg posts: RedditPost)

    @Query("select * from posts")
    fun queryAll() : Cursor

    @Query("SELECT * FROM posts WHERE name = :subreddit")
    fun postsBySubreddit(subreddit: String): Cursor
}
