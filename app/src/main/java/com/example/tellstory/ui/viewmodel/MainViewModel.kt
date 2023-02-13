package com.example.tellstory.ui.viewmodel

import androidx.lifecycle.*
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiConfig
import com.example.tellstory.coredata.remote.GetAllStoriResponse
import com.example.tellstory.coredata.remote.ListStoryItems
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val preferences: UserDataPreferences) : ViewModel() {


    private var _listStory = MutableLiveData<GetAllStoriResponse>()
    val listStory: LiveData<GetAllStoriResponse> get() = _listStory

    //are these work?
    private var _userName = MutableLiveData<ListStoryItems>()
    val userName: LiveData<ListStoryItems> get() = _userName

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
        apiService.enqueue(object : Callback<GetAllStoriResponse> {
            override fun onResponse(
                call: Call<GetAllStoriResponse>,
                response: Response<GetAllStoriResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    _listStory.postValue(body!!)
                }
            }

            override fun onFailure(call: Call<GetAllStoriResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    companion object {
        private val BEARER = "Bearer "
    }

}