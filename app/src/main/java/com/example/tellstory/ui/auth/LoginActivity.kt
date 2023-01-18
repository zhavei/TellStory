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
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loginViewModel.getUser().observe(this) { user ->
            this.storyUser = user
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.etlogin.toString().trim()
            val pass = binding.etlogin2.toString().trim()
            when {
                email.isEmpty() -> {
                    binding.etlogin.error = getString(R.string.empty_email)
                }
                pass.isEmpty() -> {
                    binding.etlogin2.error = getString(R.string.empty_password)
                }
                else -> {
                    userLogin(email, pass)
                    loginViewModel.userLogin()
                    }
                }
            }

        binding.apply {
            btnToRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun userLogin(email: String, pass: String) {
        val service = apiService.loginService(email, pass)
        //val service = ApiConfig.getApiService(this).loginService(email, pass)

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
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        " login ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

}