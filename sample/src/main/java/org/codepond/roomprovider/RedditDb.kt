package org.codepond.roomprovider

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import org.codepond.App

@Database(
        entities = [(RedditPost::class),(RedditUser::class)],
        version = 2,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RedditDb : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory: Boolean): RedditDb {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, RedditDb::class.java)
            } else {
                Room.databaseBuilder(context, RedditDb::class.java, "reddit.db")
            }
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }

        val db by lazy {
            create(App.application,false)
        }
    }

    abstract fun posts(): RedditPostDao
    abstract fun users(): RedditUserDao
}
