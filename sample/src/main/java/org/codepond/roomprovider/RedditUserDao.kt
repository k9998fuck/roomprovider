package org.codepond.roomprovider

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RedditUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: RedditUser)

    @Query("select * from users")
    fun queryAll() : Cursor
}
