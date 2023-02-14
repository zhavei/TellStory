package com.example.tellstory.ui.viewmodel

import androidx.lifecycle.*
import com.example.tellstory.common.UserDataPreferencesOld
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiConfigOld
import com.example.tellstory.coredata.remote.GetAllStoriesResponseOld
import com.example.tellstory.coredata.remote.ListStoryItems
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val preferences: UserDataPreferencesOld) : ViewModel() {


    private var _listStory = MutableLiveData<GetAllStoriesResponseOld>()
    val listStory: LiveData<GetAllStoriesResponseOld> get() = _listStory

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
        val apiService = ApiConfigOld.getApiService().getAllStoriesService(BEARER + token)
        apiService.enqueue(object : Callback<GetAllStoriesResponseOld> {
            override fun onResponse(
                call: Call<GetAllStoriesResponseOld>,
                response: Response<GetAllStoriesResponseOld>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    _listStory.postValue(body!!)
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponseOld>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    companion object {
        private val BEARER = "Bearer "
    }

}