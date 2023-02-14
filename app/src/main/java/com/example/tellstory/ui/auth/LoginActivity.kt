package com.example.tellstory.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferencesOld
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiServiceOld
import com.example.tellstory.coredata.remote.LoginResponseOld
import com.example.tellstory.databinding.ActivityLoginBinding
import com.example.tellstory.ui.main.MainActivity
import com.example.tellstory.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactories.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        playAnimation()

        loginViewModel.getUser().observe(this) { user ->
            this.storyUser = user
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPass.text.toString().trim()
            when {
                email.isEmpty() -> {
                    binding.etEmail.error = getString(R.string.empty_email)
                }
                pass.isEmpty() -> {
                    binding.etPass.error = getString(R.string.empty_password)
                }
                else -> {
                    userLogin(email, pass)
                    loginViewModel.userLogin()
                    }
                }
            }

        binding.apply {
            tvToRegister.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

    }

    private fun userLogin(email: String, pass: String) {
        showLoading(true)
        val service = apiServiceOld.loginService(email, pass)

        service.enqueue(object : Callback<LoginResponseOld> {
            override fun onResponse(call: Call<LoginResponseOld>, response: Response<LoginResponseOld>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    loginViewModel.userToken(
                        StoryUser(
                            storyUser.userEmail,
                            body.loginResultOld.name,
                            body.loginResultOld.token,
                            storyUser.userPass,
                            false
                        )
                    )

                    val userWelCome = body.loginResultOld.name
                    toMainActivity(userWelCome)
                    Toast.makeText(
                        this@LoginActivity,
                        " Welcome $userWelCome",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    showLoading(false)
                    Log.d(TAG, "is login success? =  ${response.message()}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid password or Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponseOld>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LoginActivity, "Internet Disconnected", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun toMainActivity(name: String) {
        val getName = intent.getStringExtra(LOGIN_EXTRA) //from register
        if (getName != null && getName.isNotEmpty()) {
            Intent(this@LoginActivity, MainActivity::class.java).also {
                it.putExtra(MainActivity.MAIN_EXTRA, name)
                startActivity(it)
                finish()
            }
        } else if (name.isEmpty()) {
            Intent(this@LoginActivity, MainActivity::class.java).also {
                it.putExtra(MainActivity.MAIN_EXTRA, getName)
                startActivity(it)
                finish()
            }
        } else {
            Intent(this@LoginActivity, MainActivity::class.java).also {
                it.putExtra(MainActivity.MAIN_EXTRA, name)
                startActivity(it)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progLogin.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.INVISIBLE
        } else {
            binding.progLogin.visibility = View.INVISIBLE
            binding.btnLogin.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.are_you_sure)
        builder.setPositiveButton(R.string.yes_dialog) { _, _ -> finishAffinity() }
        builder.setNegativeButton(R.string.no_dialog) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun setupView() {
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.cardViewMain, View.TRANSLATION_X, -20f, 50f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        ObjectAnimator.ofFloat(binding.tvAppName, View.TRANSLATION_X, -20f, 50f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val loginText = ObjectAnimator.ofFloat(binding.tvLoginText, View.ALPHA, 1f).setDuration(500)
        val loginLinear = ObjectAnimator.ofFloat(binding.linearLayout2, View.ALPHA, 1f).setDuration(500)
        val etlogin = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val etPass = ObjectAnimator.ofFloat(binding.etPass, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val tvDontHaveAcc =
            ObjectAnimator.ofFloat(binding.dontHaveAccount, View.ALPHA, 1f).setDuration(500)
        val singUp = ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(loginText, etlogin, etPass)
        }

        AnimatorSet().apply {
            playSequentially( loginLinear, together, btnLogin, tvDontHaveAcc, singUp)
            start()
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        val LOGIN_EXTRA = "login_extra"
    }

}