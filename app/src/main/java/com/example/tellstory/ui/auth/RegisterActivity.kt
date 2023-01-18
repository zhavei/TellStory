package com.example.tellstory.ui.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.model.StoryUser
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.databinding.ActivityLoginBinding
import com.example.tellstory.databinding.ActivityRegisterBinding
import com.example.tellstory.ui.viewmodel.LoginViewModel
import com.example.tellstory.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService
    private lateinit var storyUser: StoryUser
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val signUpViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding.btnLogin.setOnClickListener {
            val name = binding.etlogin1.toString().trim()
            val email = binding.etlogin.toString().trim()
            val pass = binding.etlogin2.toString().trim()
            when {
                name.isEmpty() -> {
                    binding.etlogin1.error = "name cannot empty"
                }
                email.isEmpty() -> {
                    binding.etlogin.error = getString(R.string.empty_email)
                }
                pass.isEmpty() -> {
                    binding.etlogin2.error = getString(R.string.empty_password)
                }
                else -> {
                    register(name, email, pass)
                }
            }
        }

    }

    private fun register(name: String, email: String, pass: String) {

        signUpViewModel.apply {
            newRegister(name, email, pass)
        }
    }
}