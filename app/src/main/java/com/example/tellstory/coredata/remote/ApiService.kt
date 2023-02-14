package com.example.tellstory.coredata.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun loginService(
        @Body loginRequestBody: LoginRequestBody
    ): Response<LoginResponse>


    @POST("register")
    suspend fun registerService(
        @Body registerRequestBody: RegisterRequestBody
    ): Response<MainResponse>


    @Multipart
    @POST("stories")
    suspend fun addNewStoryService(
        @Header("Authorization") token: String,
        @Part("description") desc: RequestBody,
        @Part("lat") lat: Float? = null,
        @Part("lon") lon: Float? = null,
        @Part file: MultipartBody.Part
    ): Response<MainResponse>


    @GET("stories")
    suspend fun listStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): Response<GetAllStoriesResponse>

    @GET("stories/{id}")
    suspend fun detailStory(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
    ): Response<DetailsStory>

}