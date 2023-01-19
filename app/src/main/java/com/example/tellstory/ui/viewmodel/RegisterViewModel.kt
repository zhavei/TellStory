package com.example.tellstory.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiConfig
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.coredata.remote.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RegisterViewModel(private val preferences: UserDataPreferences) : ViewModel() {

    @Inject
    lateinit var apiService: ApiService

    fun saveNewAuth(user: StoryUser) {
        viewModelScope.launch {
            preferences.saveUser(user)
        }
    }

    private var _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    fun newRegister(name: String, email: String, pass: String) {
        //val registerService = apiService.registerService(name, email, pass)
        val registerService = ApiConfig.getApiService().registerService(name, email, pass)

        registerService.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _toastMessage.value = "register ${body.message}"
                    if (body.message != "Email is already taken") {
                        saveNewAuth(
                            StoryUser(
                                email,
                                name,
                                "",
                                pass,
                                false
                            )
                        )
                        Log.d("registerActivity", response.message())
                    }
                } else {
                    Log.e("registerActivity", response.message())
                }
                _toastMessage.value = "fail: ${response.message()}"
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("registerActivity", "fail${t.message}")
                t.printStackTrace()
            }

        })

    }
}