package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.launch

class SplashViewModel(private val tellStoryRepository: TellStoryRepository) : ViewModel() {

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> get() = _isLogin

    companion object {
        private const val TAG = "SplashViewModel"
    }

    fun isUserLogin() {
        viewModelScope.launch {

            val token = tellStoryRepository.observeUserToken()
            _isLogin.value = if (token != null && token.isNotEmpty()) true else false

            val chekUserName = tellStoryRepository.observeUserName()
            Log.d(TAG, "check username : ${chekUserName.toString()}")
        }

    }


}