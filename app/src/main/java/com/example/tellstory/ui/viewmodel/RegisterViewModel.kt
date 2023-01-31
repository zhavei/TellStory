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

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun newRegister(name: String, email: String, pass: String) {
        _loading.value = true

        val registerService = ApiConfig.getApiService().registerService(name, email, pass)

        registerService.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {

                if (response.isSuccessful) {
                    _loading.value = true //loading
                    _statusMessage.value = true
                    val body = response.body()
                    if (body?.error != true && body != null) {
                        saveNewAuth(
                            StoryUser(
                                email,
                                name,
                                "",
                                pass,
                                false
                            )
                        )
                        _toastMessage.postValue(" this ${body.message}")
                        Log.d("registerActivity", body.message)
                    } else {
                        if (body?.error == true && body.message == "Email is already taken") {
                            /*saveNewAuth(
                                StoryUser(
                                    email,
                                    name,
                                    "",
                                    pass,
                                    false
                                )
                            )*/
                            _toastMessage.postValue(" this ${body.message}")
                            _loading.value = true //loading
                            _statusMessage.value = false
                            Log.e("registerActivity", response.message())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loading.value = false //loading
                _statusMessage.value = false
                Log.d("registerActivity", "fail${t.message}")
                t.printStackTrace()
            }

        })

    }
}