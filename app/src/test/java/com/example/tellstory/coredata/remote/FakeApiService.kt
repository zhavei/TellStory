package com.example.storyapp.data

import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.coredata.remote.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {
    override suspend fun loginService(loginRequestBody: LoginRequestBody): Response<LoginResponse> {
        return Response.success(
            LoginResponse(
                error = false,
                message = "success",
                loginResult = LoginResult(
                    userId = "it",
                    name = "user",
                    token = "token"
                )
            )
        )
    }

    override suspend fun registerService(registerRequestBody: RegisterRequestBody): Response<MainResponse> {
        return Response.success(
            MainResponse(
                error = false,
                message = "success",
            )
        )
    }

    override suspend fun addNewStoryService(
        token: String,
        desc: RequestBody,
        lat: Float?,
        lon: Float?,
        file: MultipartBody.Part
    ): Response<MainResponse> {
        return Response.success(
            MainResponse(
                error = false,
                message = "success",
            )
        )
    }

    override suspend fun listStories(
        token: String,
        page: Int,
        size: Int,
        location: Int
    ): Response<GetAllStoriesResponse> {
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

        val response = GetAllStoriesResponse(error = false, message = null, stories)

        return Response.success(response)
    }

    override suspend fun detailStory(token: String, userId: String): Response<DetailsStory> {
        return Response.success(
            DetailsStory(
                error = false,
                message = "",
                story = MainStory(
                    photoUrl = "photo",
                    createdAt = "createdAt",
                    name = "name",
                    description = "desc",
                    lon = null,
                    id = "it.toString()",
                    lat = null,
                )
            )
        )
    }
}