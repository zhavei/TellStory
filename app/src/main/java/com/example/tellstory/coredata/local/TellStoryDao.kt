package com.example.tellstory.coredata.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tellstory.coredata.model.MainStory

@Dao
interface TellStoryDao {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<MainStory>)

    @Query("SELECT * FROM main_story")
    fun getAllStory(): PagingSource<Int, MainStory>

    @Query("DELETE FROM main_story")
    suspend fun deleteAll()


}