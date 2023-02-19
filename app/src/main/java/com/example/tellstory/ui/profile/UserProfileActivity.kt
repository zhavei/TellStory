package com.example.tellstory.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tellstory.R
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.databinding.ActivityUserProfileBinding
import com.example.tellstory.ui.auth.LoginActivity
import com.example.tellstory.ui.main.MainActivity
import com.example.tellstory.ui.viewmodel.UserProfileViewModel

class UserProfileActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityUserProfileBinding.inflate(layoutInflater)
    }

    private val userProfileViewModel: UserProfileViewModel by viewModels {
        ViewModelFactories.getInstance(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userProfileViewModel.loading.observe(this) {
            showLoading(it)
        }

        //region observe logout status
        binding.tvLogout2.setOnClickListener {
            userProfileViewModel.signOut()
        }

        userProfileViewModel.signOut.observe(this) {
            startActivity(Intent(this@UserProfileActivity, LoginActivity::class.java))
            finishAffinity()
        }
        //endregion

        //welcome user
        userProfileViewModel.getTheName()
        userProfileViewModel.welcomeUser.observe(this) {
            Log.d("userProfile", "log welcome : $it")
            binding.tvAppName.text = it
            binding.tvExit.text = it
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

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.progress.visibility = View.GONE
        }
    }

    companion object {
        val USER_PROFILE_EXTRA = "user_profile"
    }
}