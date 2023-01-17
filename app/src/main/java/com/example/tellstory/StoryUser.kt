package com.example.tellstory

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryUser(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val userName: String,

    @field:SerializedName("token")
    val userToken: String
): Parcelable
