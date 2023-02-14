package com.example.tellstory.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.databinding.ActivitySplashBinding
import com.example.tellstory.ui.auth.LoginActivity
import com.example.tellstory.ui.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels<SplashViewModel> {
        ViewModelFactories.getInstance(application)
    }

    private val SPLASH_TIME_OUT: Long = 500 // 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            observeLogin()
        }, SPLASH_TIME_OUT)


    }

    private fun observeLogin() {
        viewModel.isLogin.observe(this) { isUserAlreadyLogin ->
            val activityClass = if (isUserAlreadyLogin) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, activityClass))
            finish()
            Log.d(TAG, "check the name:  $isUserAlreadyLogin")
        }

        viewModel.isUserLogin()
    }

    companion object {
        private val TAG = SplashActivity::class.simpleName
    }
}