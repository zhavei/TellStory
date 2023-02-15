package com.example.tellstory.coredata.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceOld {

    @FormUrlEncoded
    @POST("login")
    fun loginService(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponseOld>


    @FormUrlEncoded
    @POST("register")
    fun registerService(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    fun getAllStoriesService(
        @Header("Authorization") token: String,
    ): Call<GetAllStoriesResponseOld>


    @Multipart
    @POST("stories")
    fun addNewStoryService(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddNewStoryResponse>

}