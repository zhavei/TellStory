package com.example.tellstory.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class UserDataPreferences private constructor(private val userData: DataStore<Preferences>) {

    suspend fun storeUserName(name: String?) {
        userData.edit { userName ->
            if (name != null) {
                userName[USER_NAME] = name
            }
        }
    }

    suspend fun getUserName(): String? {
        val user = userData.data.first().get(USER_NAME)
        return if (user.isNullOrEmpty()) null else user
    }


    suspend fun storeToken(token: String?) {
        userData.edit { preferences ->
            if (token != null) {
                preferences[USER_TOKEN] = token
            }
        }
    }

    suspend fun getToken(): String? {
        val token = userData.data.first().get(USER_TOKEN)
        return if (token.isNullOrEmpty()) null else token
    }

    suspend fun clearToken(token: String?) {
        userData.edit { preference ->
            preference[USER_TOKEN] = token ?: ""
            preference[USER_NAME] = token ?: ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserDataPreferences? = null

        private val USER_TOKEN = stringPreferencesKey("user_token")
        private val USER_NAME = stringPreferencesKey("user_name")

        fun getInstance(dataStore: DataStore<Preferences>): UserDataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserDataPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}