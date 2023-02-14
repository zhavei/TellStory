package com.example.tellstory.coredata.local

import android.content.Context
import androidx.room.*
import com.example.tellstory.coredata.model.MainStory

@Database(
    entities = [MainStory::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class TellStoryDatabase : RoomDatabase() {
    abstract fun tellStoryDao(): TellStoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: TellStoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): TellStoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TellStoryDatabase::class.java, "tell_story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }


}