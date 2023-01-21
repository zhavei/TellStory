package com.example.tellstory.ui.viewmodel

import androidx.lifecycle.*
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiConfig
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.coredata.remote.GetAllStoriesResponse
import com.example.tellstory.coredata.remote.ListStoryItems
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MainViewModel(private val preferences: UserDataPreferences) : ViewModel() {

    /*@Inject
    lateinit var apiService: ApiService*/

    private var _listStory = MutableLiveData<GetAllStoriesResponse>()
    val listStory: LiveData<GetAllStoriesResponse> = _listStory

    private var _userName = MutableLiveData<ListStoryItems>()
    val userName: LiveData<ListStoryItems> = _userName

    fun isUserLogin(): LiveData<StoryUser> {
        return preferences.getUserStory().asLiveData()
    }

    //to get the user token
    fun getUser(): LiveData<StoryUser> {
        return preferences.getUserStory().asLiveData()
    }

    fun signOut() {
        viewModelScope.launch {
            preferences.logOutUser()
        }
    }

    fun getListStories(token: String) {
        val apiService = ApiConfig.getApiService().getAllStoriesService(BEARER + token)
        //val apiService = apiService.getAllStoriesService(BEARER + token)
        apiService.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>,
                response: Response<GetAllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    _listStory.postValue(body!!)
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    companion object {
        private val BEARER = "Bearer "
    }

}