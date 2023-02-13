package com.example.tellstory.coredata.remote

import com.example.tellstory.coredata.model.MainStory
import com.google.gson.annotations.SerializedName

class NetworkResponses {


    data class LoginResponse(
        @field:SerializedName("error")
        val error: Boolean = false,
        @field:SerializedName("message")
        val message: String = "",
        @field:SerializedName("loginResult")
        val loginResult: LoginResult? = null,
    )

    data class GetAllStoriesResponse(
        @field:SerializedName("error")
        val error: Boolean = false,
        @field:SerializedName("message")
        val message: String? = null,
        @field:SerializedName("listStory")
        val listStory: List<MainStory>? = null,
    )

    data class ListStoryItems(
        @field:SerializedName("error")
        val error: Boolean = false,
        @field:SerializedName("message")
        val message: String = "",
        @field:SerializedName("story")
        val story: MainStory? = null,
    )

    data class MainResponse(
        @field:SerializedName("error")
        val error: Boolean? = null,
        @field:SerializedName("message")
        val message: String? = null,
    )

}