package com.example.tellstory.coredata.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class LoginResponseOld(

    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("loginResultOld") val loginResultOld: LoginResultOld,
)

data class LoginResultOld(

    @field:SerializedName("userId") val userId: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("token") val token: String
)

data class RegisterResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

data class AddNewStoryResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

data class GetAllStoriesResponseOld(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("listStory") val listStoryItems: List<ListStoryItems>
)

@Parcelize
data class ListStoryItems(

    @field:SerializedName("id") val id: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("photoUrl") val photoUrl: String,
    @field:SerializedName("createdAt") val createdAt: String
) : Parcelable