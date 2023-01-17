package com.example.tellstory.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.coredata.model.StoryUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_settings")

class Preferences(private val context: Context) {

    private val userPreferencesDataStore = context.dataStore

    fun getUserStory(): Flow<StoryUser> {
        return userPreferencesDataStore.data.map {
            StoryUser(
                it[KEY_ID] ?: "",
                it[KEY_USER_NAME] ?: "",
                it[KEY_USER_TOKEN] ?: "",
            )

        }
    }

    suspend fun saveUser(user: StoryUser) {
        userPreferencesDataStore.edit {
            it[KEY_ID] = user.userId
            it[KEY_USER_NAME] = user.userName
            it[KEY_USER_TOKEN] = user.userToken
        }
    }

    suspend fun logOutUser(user: StoryUser) {
        userPreferencesDataStore.edit {
            it[KEY_ID] = ""
            it[KEY_USER_NAME] = ""
            it[KEY_USER_TOKEN] = ""
        }
    }

    companion object {
        val KEY_ID = stringPreferencesKey("user_id")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_TOKEN = stringPreferencesKey("user_token")
    }

}
