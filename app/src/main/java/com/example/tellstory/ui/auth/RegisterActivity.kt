package com.example.tellstory.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import com.example.tellstory.R
import com.example.tellstory.common.UserDataPreferences
import com.example.tellstory.common.ViewModelFactory
import com.example.tellstory.coredata.remote.ApiService
import com.example.tellstory.databinding.ActivityRegisterBinding
import com.example.tellstory.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val signUpViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.btnregister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPass.text.toString().trim()
            when {
                name.isEmpty() -> {
                    binding.etName.error = "name cannot empty"
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


    }

    private fun register(name: String, email: String, pass: String) {

        toLoginActivity(name)

        signUpViewModel.apply {
            newRegister(name, email, pass)
            showLog(this.toastMessage)

        }
    }

    private fun toLoginActivity(name: String) {
        Intent(this@RegisterActivity, LoginActivity::class.java).also {
            it.putExtra(LoginActivity.LOGIN_EXTRA, name)
            startActivity(it)
            finish()
        }
    }

    private fun showLog(toastMessage: LiveData<String>) {
        Log.d(TAG, toastMessage.toString())

    }

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }
}