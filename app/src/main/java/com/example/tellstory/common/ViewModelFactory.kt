package com.example.tellstory.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tellstory.ui.viewmodel.*

/*
@Suppress("UNCHECKED_CAST")
class ViewModelFactory (
    private val userDataPreferencesOld: UserDataPreferencesOld
) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userDataPreferencesOld) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userDataPreferencesOld) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userDataPreferencesOld) as T
            }
            modelClass.isAssignableFrom(AddNewStoryViewModel::class.java) -> {
                AddNewStoryViewModel(userDataPreferencesOld) as T
            }

            else -> throw IllegalArgumentException("Unrecognized ViewModel class: " + modelClass.name)
        }
    }

}*/
