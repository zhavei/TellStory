package com.example.tellstory.coredata.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryUser(
    @field:SerializedName("email")
    val userEmail: String,

    @field:SerializedName("name")
    val userName: String,

    @field:SerializedName("token")
    val userToken: String,

    @field:SerializedName("password")
    val userPass: String,

    @field:SerializedName("is_login")
    val isUserLogin: Boolean,


): Parcelable
