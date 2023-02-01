package com.example.tellstory.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.coredata.remote.LoginResponse
import com.example.tellstory.databinding.ActivityLoginBinding
import com.example.tellstory.ui.main.MainActivity
import com.example.tellstory.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService
    private lateinit var storyUser: StoryUser
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
            btnToRegister.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

    }

    private fun userLogin(email: String, pass: String) {
        showLoading(true)
        val service = apiService.loginService(email, pass)

        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    loginViewModel.userToken(
                        StoryUser(
                            storyUser.userEmail,
                            body.loginResult.name,
                            body.loginResult.token,
                            storyUser.userPass,
                            false
                        )
                    )

                    val userWelCome = body.loginResult.name
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
                        " login ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
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
            binding.btnLogin.visibility = View.GONE
        } else {
            binding.progLogin.visibility = View.GONE
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

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        val LOGIN_EXTRA = "login_extra"
    }

}