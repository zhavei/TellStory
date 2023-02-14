package com.example.tellstory.coredata.remote

import com.example.tellstory.coredata.model.MainStory
import com.google.gson.annotations.SerializedName

/**
 * network responses appear here
 */

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

data class DetailsStory(
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

data class LoginResult(
    @field:SerializedName("userId")
    val userId: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("token")
    val token: String? = null,
)

/**
 * network request body appear here
 */

data class AddNewStoryRequest(
    @SerializedName("description")
    val description: String,
    @SerializedName("lat")
    val lat: Float?,
    @SerializedName("lon")
    val lon: Float?,
)

data class LoginRequestBody(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequestBody(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

