package com.example.tellstory.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.databinding.ActivityMainBinding
import com.example.tellstory.ui.auth.LoginActivity
import com.example.tellstory.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService
    private lateinit var storyUser: StoryUser
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /* mainViewModel.isUserLogin().observe(this, {
             if (!it.isUserLogin) {
                 val intent = Intent(this@MainActivity, LoginActivity::class.java)
                 startActivity(intent)
                 finish()
             }
         })*/

        setupAction()
    }

    private fun setupAction() {
        mainViewModel.isUserLogin().observe(this) { user ->
            if (user.isUserLogin) {
                Toast.makeText(this, "Hello ${user.userName}", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
}