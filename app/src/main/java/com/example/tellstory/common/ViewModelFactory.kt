package com.example.tellstory.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tellstory.ui.viewmodel.LoginViewModel
import com.example.tellstory.ui.viewmodel.MainViewModel
import com.example.tellstory.ui.viewmodel.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (private val userDataPreferences: UserDataPreferences) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userDataPreferences) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userDataPreferences) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userDataPreferences) as T
            }
            else -> throw IllegalArgumentException("Unrecognized ViewModel class: " + modelClass.name)
        }
    }

}