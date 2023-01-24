package com.example.tellstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.AddNewStoryResponse
import com.example.tellstory.coredata.remote.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddNewStoryViewModel(
    private val preferences: UserDataPreferences,
) : ViewModel() {

    fun getUser(): LiveData<StoryUser> {
        return preferences.getUserStory().asLiveData()
    }

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> get() = _status

    fun postNewStory(file: MultipartBody.Part, description: RequestBody, token: String) {
        _loading.value = true
        val requestService =
            ApiConfig.getApiService().addNewStoryService(BEARER + token, file, description)
        /*val requestService = apiService.addNewStoryService(BEARER + token, file, description)*/
        requestService.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                val body = response.body()
                if (body != null) {
                    if (response.isSuccessful && body.error) {
                        _loading.value = false
                        _status.value = true
                    } else {
                        _status.value = false
                    }
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _loading.value = false
                _status.value = false
            }
        })
    }

    companion object {
        private val BEARER = "Bearer "
    }

}