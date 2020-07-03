package org.codepond.roomprovider

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface RedditUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<RedditUser>)
}
