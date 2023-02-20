package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(private val tellStoryRepository: TellStoryRepository) : ViewModel() {


    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> get() = _showLoading

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> get() = _isLogin

    private val _showWelcome = MutableLiveData<String>()
    val showWelcome: LiveData<String> get() = _showWelcome

    fun userLogin(email: String, password: String) {
        setLoading(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                //am add this to get the name
                val name = tellStoryRepository.getUserName(email, password)

                val result = tellStoryRepository.login(email, password)

                if (result?.isNotEmpty() == true) {
                    setAuthenticated(true)
                    setLoading(false)
                    Log.d(TAG, "login name: $name")
                    Log.d(TAG, "login token: $result")
                    setSnackbarText("welcome $name")

                } else {
                    setLoading(false)
                    setAuthenticated(false)
                    setSnackbarText("cannot login")
                    Log.e(TAG, " is login get token: ${result.toString()}")
                }
            } catch (e: Exception) {
                setLoading(false)
                setAuthenticated(false)
                setSnackbarText("Either Email or Password is Invalid")
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _showLoading.postValue(isLoading)
    }

    private fun setAuthenticated(isAuthenticated: Boolean) {
        _isLogin.postValue(isAuthenticated)
    }

    private fun setSnackbarText(message: String) {
        _showWelcome.postValue(message)
    }

    companion object {
        private val TAG = LoginViewModel::class.simpleName
    }

}