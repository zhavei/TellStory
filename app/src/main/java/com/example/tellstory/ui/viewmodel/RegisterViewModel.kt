package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiConfig
import com.example.tellstory.coredata.remote.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterViewModel (
    private val preferences: UserDataPreferences
) : ViewModel() {

    fun saveNewAuth(user: StoryUser) {
        viewModelScope.launch {
            preferences.saveUser(user)
        }
    }

    private var _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private var _statusMessage = MutableLiveData<Boolean>()
    val statusMessage: LiveData<Boolean> get() = _statusMessage

    fun newRegister(name: String, email: String, pass: String) {
        val registerService = ApiConfig.getApiService().registerService(name, email, pass)

        registerService.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
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
                        _toastMessage.value = body.message.toString()
                        _statusMessage.value = true
                        Log.d("registerActivity", response.message())
                    }
                } else {
                    if (body != null) {
                        if (body.message == "Email is already taken") {
                            saveNewAuth(
                                StoryUser(
                                    email,
                                    name,
                                    "",
                                    pass,
                                    false
                                )
                            )
                            _toastMessage.value = "Email is already taken"
                            _statusMessage.value = false
                        }
                        Log.e("registerActivity", response.message())
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("registerActivity", "fail${t.message}")
                t.printStackTrace()
            }

        })

    }
}