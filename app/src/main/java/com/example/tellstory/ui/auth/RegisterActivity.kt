package com.example.tellstory.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferencesOld
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.remote.ApiServiceOld
import com.example.tellstory.databinding.ActivityRegisterBinding
import com.example.tellstory.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var apiServiceOld: ApiServiceOld
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val signUpViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(UserDataPreferencesOld.getInstance(userDataStore))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAnimation()


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
                else -> register(name, email, pass)
            }
        }

        binding.apply {
            tvTologin.setOnClickListener {
                Intent(this@RegisterActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }


    }

    private fun register(name: String, email: String, pass: String) {
        sendToLoginActivity(name) //send name from register

        signUpViewModel.apply {
            newRegister(name, email, pass)
            loading.observe(this@RegisterActivity) {
                showLoading(it)
            }
            statusMessage.observe(this@RegisterActivity) {
                showStatus(it)
            }
            toastMessage.observe(this@RegisterActivity) {
                Log.d(TAG, "testing toast $it")
                Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
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