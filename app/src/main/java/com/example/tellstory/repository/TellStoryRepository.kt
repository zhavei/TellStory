package com.example.tellstory.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.local.TellStoryDatabase
import com.example.tellstory.coredata.local.TellStoryRemoteMediator
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.coredata.remote.AddNewStoryRequest
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.coredata.remote.LoginRequestBody
import com.example.tellstory.coredata.remote.RegisterRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class TellStoryRepository private constructor(
    private val apiService: ApiService,
    private val pref: UserDataPreferences,
    private val database: TellStoryDatabase
) {

    /**
     * below this manage user authentication and authorization.
     * When a user logs in to a system or application, their credentials are verified against the user repository.
     */

    suspend fun observeUserName(): String? = withContext(Dispatchers.IO) {
        pref.getUserName()
    }

    suspend fun getUserName(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            val request = LoginRequestBody(email = email, password = password)
            val response = apiService.loginService(request)
            if (response.isSuccessful) {
                // this to get username
                val username = response.body()?.loginResult?.name
                pref.storeUserName(username)
                username
            } else {
                throw Exception(response.body()?.message.toString())
            }
        }
    }

    suspend fun login(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            val request = LoginRequestBody(email = email, password = password)
            val response = apiService.loginService(request)

            if (response.isSuccessful) {
                //get username & token
                val username = response.body()?.loginResult?.name
                pref.storeUserName(username)

                val token = response.body()?.loginResult?.token
                pref.storeToken(token)
                token + username

            } else {
                throw Exception(response.body()?.message.toString())
            }
        }
    }

    suspend fun register(userName: String, email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val registerRequest = RegisterRequestBody(userName, email, password)
            val response = apiService.registerService(registerRequest)
            Log.d("register Repository", response.body()?.message.toString())
            if (response.isSuccessful) {
                response.body()?.error == false
            } else {
                throw Exception(response.body()?.message)
            }
        }

    }

    suspend fun observeUserToken(): String? = withContext(Dispatchers.IO) {
        pref.getToken() //buat ambil token di splashscreen
    }

    suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        try {
            pref.clearToken(null)
            true
        } catch (e: Throwable) {
            throw Exception(e.message.toString())
        }
    }

    /**
     * below this is data repository is to enable effective data management and sharing.
     * By storing data in a specific location
     */

    fun fetchAllStories(): LiveData<PagingData<MainStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = TellStoryRemoteMediator(database, apiService, pref),
            pagingSourceFactory = {
                database.tellStoryDao().getAllStory()
            }
        ).liveData
    }

    //add new story
    suspend fun addNewStory(storyData: AddNewStoryRequest, photoPart: MultipartBody.Part): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer ${pref.getToken()}"
                val response = apiService.addNewStoryService(
                    token = bearerToken,
                    desc = storyData.description.toRequestBody("text/plain".toMediaType()),
                    file = photoPart,
                    lat = storyData.lat,
                    lon = storyData.lon
                )

                if (!response.isSuccessful) {
                    throw Exception(response.body()?.message.toString())
                }

                return@withContext response.body()?.error == false
            } catch (e: Throwable) {
                throw Exception(e.message.toString())
            }
        }

    //stories details
    suspend fun getStoryDetail(userId: String): MainStory? = withContext(Dispatchers.IO) {
        val bearerToken = "Bearer ${pref.getToken()}"
        val response = apiService.detailStory(token = bearerToken, userId = userId)
        if (!response.isSuccessful) throw Exception(response.body()?.message.toString())
        response.body()?.story
    }


    //stories in gmaps
    /*suspend fun getAllStoriesMap(): List<Story> {
        val stories = mutableListOf<Story>()
        withContext(Dispatchers.IO) {
            try {
                val userToken = pref.getToken()
                val bearerToken = "Bearer $userToken"
                val response = apiServices.getAllStories(
                    authToken = bearerToken,
                    page = 1,
                    size = 100,
                    location = 1
                )
                if (response.isSuccessful) {
                    response.body()?.listStory?.forEach {
                        stories.add(
                            Story(
                                id = it.id,
                                name = it.name,
                                description = it.description,
                                photoUrl = it.photoUrl,
                                createdAt = it.createdAt,
                                lat = it.lat,
                                lon = it.lon
                            )
                        )

                    }
                }
            } catch (e: Throwable) {
                throw Exception(e.message.toString())
            }
        }
        return stories
    }*/


    companion object {
        @Volatile
        private var instance: TellStoryRepository? = null

        fun getInstance(
            apiServices: ApiService,
            pref: UserDataPreferences,
            database: TellStoryDatabase
        ): TellStoryRepository {
            if (instance == null) {
                synchronized(this) {
                    instance = TellStoryRepository(apiServices, pref, database)
                }
            }
            return instance as TellStoryRepository
        }
    }
}