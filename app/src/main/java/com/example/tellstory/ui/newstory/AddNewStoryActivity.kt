package com.example.tellstory.ui.newstory

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.databinding.ActivityAddNewStoryBinding
import com.example.tellstory.ui.viewmodel.AddNewStoryViewModel

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
class AddNewStoryActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddNewStoryBinding.inflate(layoutInflater)
    }

    private val addNewStoryViewModel: AddNewStoryViewModel by viewModels() {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        hideSystemUI()


    }


    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}