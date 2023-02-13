package com.example.tellstory.coredata.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * this main table story used around across main data app
 */

@Entity(tableName = "main_story")
data class MainStory(
    @field:SerializedName("id")
    @PrimaryKey val id: String = "",
    @field:SerializedName("name")
    val name: String = "",
    @field:SerializedName("description")
    val description: String = "",
    @field:SerializedName("photoUrl")
    val photoUrl: String = "",
    @field:SerializedName("createdAt")
    val createdAt: String = "",
    @field:SerializedName("lat")
    val lat: Double?,
    @field:SerializedName("lon")
    val lon: Double?,
    )
