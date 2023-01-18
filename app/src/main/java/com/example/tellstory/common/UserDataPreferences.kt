package com.example.tellstory.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tellstory.coredata.model.StoryUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataPreferences private constructor(private val userDataStore: DataStore<Preferences>) {

    fun getUserStory(): Flow<StoryUser> {
        return userDataStore.data.map { getuser ->
            StoryUser(
                getuser[KEY_USER_EMAIL] ?: "",
                getuser[KEY_USER_NAME] ?: "",
                getuser[KEY_USER_TOKEN] ?: "",
                getuser[KEY_USER_PASS] ?: "",
                getuser[KEY_IS_LOGIN] ?: false,
            )

        }
    }

    suspend fun saveUser(user: StoryUser) {
        userDataStore.edit { saveUser ->
            saveUser[KEY_USER_EMAIL] = user.userEmail
            saveUser[KEY_USER_NAME] = user.userName
            saveUser[KEY_USER_PASS] = user.userPass
            saveUser[KEY_IS_LOGIN] = user.isUserLogin
        }
    }

    suspend fun login() {
        userDataStore.edit { userLogin ->
            userLogin[KEY_IS_LOGIN] = true
        }
    }

    suspend fun logOutUser(user: StoryUser) {
        userDataStore.edit { logOutUser ->
            logOutUser[KEY_IS_LOGIN] = false
            logOutUser[KEY_USER_NAME] = ""
            logOutUser[KEY_USER_PASS] = ""
            logOutUser[KEY_USER_EMAIL] = ""
            logOutUser[KEY_USER_TOKEN] = ""
        }
    }

    suspend fun userToken(user: StoryUser) {
        userDataStore.edit { userToken ->
            userToken[KEY_USER_TOKEN] = user.userToken
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserDataPreferences? = null
        val KEY_IS_LOGIN = booleanPreferencesKey("user_state")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_TOKEN = stringPreferencesKey("user_token")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_PASS = stringPreferencesKey("user_pass")

        fun getInstance(dataStore: DataStore<Preferences>): UserDataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserDataPreferences(dataStore)
                INSTANCE = instance
                instance
            }

        }

    }

}
