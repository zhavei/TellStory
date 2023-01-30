package com.example.tellstory.ui.auth

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
                    //testing get user
                    loginViewModel.userNam(storyUser)
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
        val service = apiService.loginService(email, pass)

        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    loginViewModel.userToken(
                        StoryUser(
                            storyUser.userEmail,
                            storyUser.userName,
                            body.loginResult.token,
                            storyUser.userPass,
                            false
                        )
                    )
                    toMainActivity()
                    Toast.makeText(
                        this@LoginActivity,
                        " login ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.d(TAG, "is success? =  ${response.message()}")
                    Toast.makeText(
                        this@LoginActivity,
                        " login ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun toMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

}