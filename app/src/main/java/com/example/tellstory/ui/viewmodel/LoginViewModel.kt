package com.example.tellstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tellstory.common.UserDataPreferencesOld
import com.example.tellstory.coredata.model.StoryUser
import kotlinx.coroutines.launch


class LoginViewModel(private val preferences: UserDataPreferencesOld) : ViewModel() {




    fun getUser(): LiveData<StoryUser> {
        return preferences.getUserStory().asLiveData()
    }

    fun userToken(user: StoryUser) {
        viewModelScope.launch {
            preferences.userToken(user)
        }
    }

    fun userLogin() {
        viewModelScope.launch {
            preferences.login()
        }
    }

}