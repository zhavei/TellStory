package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.repository.TellStoryRepository
import com.example.tellstory.ui.profile.UserProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileViewModel(private val tellStoryRepository: TellStoryRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _signOut = MutableLiveData<Boolean>()
    val signOut: LiveData<Boolean> get() = _signOut

    private val _welcomeUser = MutableLiveData<String>()
    val welcomeUser: LiveData<String> = _welcomeUser

    fun getTheName() {
        viewModelScope.launch {
            //get the username
            val user = tellStoryRepository.observeUserName()
            _welcomeUser.value = user.toString()
            Log.d("Main viewModel", user.toString())
        }
    }

    fun signOut() {
        _loading.postValue(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = tellStoryRepository.logout()
                if (result) {
                    _loading.postValue(false)
                    _signOut.postValue(true)
                } else {
                    _loading.postValue(false)
                }
            } catch (e: Exception) {
                _loading.postValue(false)
                Log.e(TAG, "getStories: ${e.message}")
            }
        }
    }

    companion object {
        private val TAG = UserProfileActivity::class.java.simpleName
    }

}