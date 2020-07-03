package org.codepond.roomprovider

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import android.database.Cursor

@Dao
interface RedditPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<RedditPost>)

    @Query("SELECT * FROM posts WHERE name = :subreddit")
    fun postsBySubreddit(subreddit: String): Cursor
}
