package com.example.tellstory.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.databinding.ActivityUserProfileBinding
import com.example.tellstory.ui.viewmodel.UserProfileViewModel

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
class UserProfileActivity : AppCompatActivity() {

    private lateinit var storyUser: StoryUser
    private val binding by lazy {
        ActivityUserProfileBinding.inflate(layoutInflater)
    }
    private val userProfileViewModel: UserProfileViewModel by viewModels {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val getData = intent.getStringExtra(USER_PROFILE_EXTRA)
        if (getData != null && getData.isNotEmpty()) {
            binding.tvAppName.text = "hello $getData"
        } else {
            binding.tvAppName.text = getString(R.string.hello_user)
        }

        setupLanguage()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.user_profile)

    }

    private fun setupLanguage() {
        binding.tvChangeLanguange.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val USER_PROFILE_EXTRA = "user_profile"
    }
}