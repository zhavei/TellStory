package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.common.UserDataPreferencesOld
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiConfigOld
import com.example.tellstory.coredata.remote.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterViewModel (
    private val preferences: UserDataPreferencesOld
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

        val registerService = ApiConfigOld.getApiService().registerService(name, email, pass)

        registerService.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error != true && body != null && body.message != "Email is already taken") {
                        saveNewAuth(
                            StoryUser(
                                email,
                                name,
                                "",
                                pass,
                                false
                            )
                        )
                        _loading.value = false //loading
                        _statusMessage.value = true
                        _toastMessage.value =
                            " this ${body.message} Successfully" //the message is appear here.
                        Log.d("registerActivity status", body.message)
                    }
                } else {
                    val body = response.body()
                    saveNewAuth(
                        StoryUser(
                            email,
                            name,
                            "",
                            pass,
                            false
                        )
                    )
                    _loading.value = false //loading
                    _statusMessage.value = false
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loading.value = false //loading
                Log.d("registerActivity", "fail${t.message}")
                t.printStackTrace()
            }

        })

    }
}