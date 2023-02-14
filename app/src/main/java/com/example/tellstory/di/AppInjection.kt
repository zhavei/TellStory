package com.example.tellstory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.coredata.local.TellStoryDatabase
import com.example.tellstory.coredata.remote.ApiConfig
import com.example.tellstory.repository.TellStoryRepository


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_module")

object AppInjection {

    fun provideStoryRepository(context: Context): TellStoryRepository {
        val apiServices = ApiConfig.getApiService()
        val database = TellStoryDatabase.getDatabase(context)
        return TellStoryRepository.getInstance(
            apiServices,
            UserDataPreferences.getInstance(context.dataStore),
            database
        )
    }

}