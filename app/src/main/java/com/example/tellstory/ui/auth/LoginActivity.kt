package com.example.tellstory.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tellstory.R
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.databinding.ActivityLoginBinding
import com.example.tellstory.ui.main.MainActivity
import com.example.tellstory.ui.viewmodel.LoginViewModel


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

        loginViewModel.isLogin.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }

        loginViewModel.showLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.showWelcome.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.trim().toString()
            val pass = binding.etPass.text?.trim().toString()
            when {
                email.isEmpty() -> {
                    binding.etEmail.error = getString(R.string.empty_email)
                }
                pass.isEmpty() -> {
                    binding.etPass.error = getString(R.string.empty_password)
                }
                else -> {
                    loginViewModel.userLogin(email, pass)
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