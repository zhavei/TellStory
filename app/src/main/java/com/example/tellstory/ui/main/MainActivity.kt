package com.example.tellstory.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.coredata.remote.ListStoryItems
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

        //region observe the user and token
        mainViewModel.getUser().observe(this) { user ->
            //get the user
            this.storyUser = user
            //get the token
            val token = user.userToken
            mainViewModel.getListStories(token)
        }
        //endregion
        setupAction()
    }

    private fun setupAction() {
        mainViewModel.isUserLogin().observe(this) { user ->
            if (user.isUserLogin) {
                //region how to welcomed the name?
                welcomedUser()
                //endregion
                setupAdapter()
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun welcomedUser() {
        mainViewModel.apply {
            userName.observe(this@MainActivity) {
                //set the welcome
                val user = it.name
                Log.d(TAG, "check the name: -> $user")
                Toast.makeText(this@MainActivity, "Hello $user", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun setupAdapter() {
        val storyAdapter = StoryAdapter()

        mainViewModel.apply {
            listStory.observe(this@MainActivity) {
                //set the adapter
                val list = it.listStoryItems
                storyAdapter.submitList(list)
            }
        }

        binding.apply {
            rvMain.layoutManager = GridLayoutManager(this@MainActivity, 2)
            rvMain.adapter = storyAdapter
        }

        storyAdapter.onItemClickCallback = object : StoryAdapter.OnItemClickcallback {
            override fun onItemClicked(name: ListStoryItems) {
                //todetail activity
                toDetailActivity(name)
            }
        }


    }

    private fun toDetailActivity(name: ListStoryItems) {
        Toast.makeText(this, "this ${name.name} status", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}