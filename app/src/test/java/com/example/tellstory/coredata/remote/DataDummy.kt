package com.example.storyapp.data

import androidx.paging.PagingData
import com.example.tellstory.coredata.model.MainStory

object DataDummy {
    fun generateDummyStories(): List<MainStory> {
        val stories: MutableList<MainStory> = arrayListOf()
        repeat(10) {
            stories.add(
                MainStory(
                    photoUrl = "photo-$it",
                    createdAt = "createdAt-$it",
                    name = "name-$it",
                    description = "desc-$it",
                    lon = null,
                    id = it.toString(),
                    lat = null,
                )
            )
        }
        return stories
    }
}