package com.example.tellstory.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tellstory.di.AppInjection
import com.example.tellstory.repository.TellStoryRepository
import com.example.tellstory.ui.viewmodel.*

class ViewModelFactories (
    private val storyRepository: TellStoryRepository): ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (instance == null) {
                synchronized(ViewModelFactory::class.java) {
                    instance = ViewModelFactory(
                        AppInjection.provideStoryRepository(context)
                    )
                }
            }

            return instance as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(storyRepository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(storyRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(storyRepository) as T
            modelClass.isAssignableFrom(AddNewStoryViewModel::class.java) -> AddNewStoryViewModel(storyRepository) as T
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> DetailsViewModel(storyRepository) as T
            else -> throw IllegalArgumentException("Unrecognized ViewModel class: " + modelClass.name)
        }
    }
}