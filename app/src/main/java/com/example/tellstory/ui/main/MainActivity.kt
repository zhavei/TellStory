package com.example.tellstory.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.coredata.remote.ListStoryItems
import com.example.tellstory.databinding.ActivityMainBinding
import com.example.tellstory.ui.auth.LoginActivity
import com.example.tellstory.ui.detail.DetailsActivity
import com.example.tellstory.ui.newstory.AddNewStoryActivity
import com.example.tellstory.ui.profile.UserProfileActivity
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

        supportActionBar?.hide()


        binding.apply {
            val getData = intent.getStringExtra(MAIN_EXTRA)
            cardViewMain.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(cardViewMain, getString(R.string.photo_profile)),
                        Pair(tvUserNameMain, getString(R.string.user_name))
                    )
                startActivity(
                    Intent(this@MainActivity, UserProfileActivity::class.java).also {
                        it.putExtra(UserProfileActivity.USER_PROFILE_EXTRA, getData)
                    },
                    optionsCompat.toBundle(),
                )
            }

            cvExit.setOnClickListener {
                mainViewModel.signOut()
            }
        }

        //region observe the user and token
        mainViewModel.getUser().observe(this) { user ->
            //get the user
            this.storyUser = user
            //get the token
            val token = user.userToken
            val userN = user.userName
            Log.d(TAG, "testing ${user.userToken}")
            Log.d(TAG, "testing $userN")
            mainViewModel.getListStories(token)
        }
        //endregion
        setupAction()

        //get userName still empty
        mainViewModel.getUser().observe(this@MainActivity) {
            this.storyUser = it
            Log.d(TAG, "testingg ${it.userName}")
        }

        // region add new story
        binding.apply {
            fabMain.setOnClickListener {
                val intent = Intent(this@MainActivity, AddNewStoryActivity::class.java)
                startActivity(intent)
                Log.d(TAG, "testing Fab")
            }
        }

        //endregion
    }

    private fun setupAction() {
        mainViewModel.isUserLogin().observe(this) { user ->
            if (user.isUserLogin) {
                //region welcomed the name
                welcomedUser(user.userName)
                //endregion
                setupAdapter()
                //setup userName
                setupName()
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun welcomedUser(userN: String) {
        mainViewModel.apply {
            userName.observe(this@MainActivity) {
                //set the welcome
                val user = it.name
                Log.d(TAG, "check the name: -> $user")
                Toast.makeText(this@MainActivity, "Hello ${userN.toString()}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupName() {
        val getData = intent.getStringExtra(MAIN_EXTRA)
        binding.tvUserNameMain.text = getData
        Log.d(TAG, "testing to $getData")
    }

    private fun setupAdapter() {
        var storyAdapter = StoryAdapter()

        mainViewModel.apply {
            listStory.observe(this@MainActivity) {
                //set the adapter
                if (it != null) {
                    val list = it.listStoryItems
                    storyAdapter.submitList(list)
                }
            }
        }

        binding.apply {
            rvMain.layoutManager = GridLayoutManager(this@MainActivity, 2)
            rvMain.setHasFixedSize(true)
            rvMain.adapter = storyAdapter
        }

        storyAdapter.onItemClickCallback = object : StoryAdapter.OnItemClickcallback {
            override fun onItemClicked(name: ListStoryItems) {
                //to detail activity
                toDetailActivity(name)
            }
        }

    }


    private fun toDetailActivity(name: ListStoryItems) {
        Intent(this, DetailsActivity::class.java).also {
            it.putExtra(DetailsActivity.DETAILS_EXTRA, name)
            startActivity(it)
        }

        Toast.makeText(this, "this ${name.name} status", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        const val MAIN_EXTRA = "main_activity"
    }

}