package com.example.tellstory.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tellstory.R
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.databinding.ActivityRegisterBinding
import com.example.tellstory.ui.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactories.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAnimation()

        registerViewModel.loading.observe(this) {
            showLoading(it)
        }

        registerViewModel.registerStatus.observe(this) { isRegistered ->
            if (isRegistered) {
                showConfirmationDialog(isRegistered)
            }
        }

        registerViewModel.showError.observe(this) {
            if (it) {
                Toast.makeText(this, R.string.email_is_used, Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnregister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPass.text.toString().trim()
            when {
                name.isEmpty() -> {
                    binding.etName.error = getString(R.string.name_cannot_empty)
                }
                email.isEmpty() -> {
                    binding.etEmail.error = getString(R.string.empty_email)
                }
                pass.isEmpty() -> {
                    binding.etPass.error = getString(R.string.empty_password)
                }
                else -> registerViewModel.newRegister(name, email, pass)
            }
        }

        binding.apply {
            tvTologin.setOnClickListener {
                Intent(this@RegisterActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    finishAffinity()
                }
            }
        }


    }

    private fun showConfirmationDialog(registered: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Account Successfully Registered")
        builder.setPositiveButton("Yes") { _, _ ->
            if (registered) {
                finish()
            }
        }
        builder.show()

    }

    private fun showStatus(statusSuccess: Boolean) {
        if (statusSuccess) {
            finish()
        } else {
            Toast.makeText(this, "Email is already taken", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingProgress.visibility = View.VISIBLE
            binding.btnregister.visibility = View.INVISIBLE
        } else {
            binding.loadingProgress.visibility = View.INVISIBLE
            binding.btnregister.visibility = View.VISIBLE
        }
    }

    private fun sendToLoginActivity(name: String) {
        Intent(this@RegisterActivity, LoginActivity::class.java).also {
            it.putExtra(LoginActivity.LOGIN_EXTRA, name)
        }
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

        val loginText = ObjectAnimator.ofFloat(binding.tvSighup, View.ALPHA, 1f).setDuration(500)
        val etName = ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f).setDuration(500)
        val etlinear = ObjectAnimator.ofFloat(binding.linearLayout, View.ALPHA, 1f).setDuration(500)
        val etRegister = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val etPass = ObjectAnimator.ofFloat(binding.etPass, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnregister, View.ALPHA, 1f).setDuration(500)
        val tvDontHaveAcc =
            ObjectAnimator.ofFloat(binding.dontHaveAccount, View.ALPHA, 1f).setDuration(500)
        val singUp = ObjectAnimator.ofFloat(binding.tvTologin, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(loginText, etlinear, etName, etRegister, etPass)
        }

        AnimatorSet().apply {
            playSequentially( together, btnLogin, tvDontHaveAcc, singUp)
            start()
        }
    }

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }
}