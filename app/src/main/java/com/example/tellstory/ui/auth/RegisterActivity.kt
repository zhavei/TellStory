package com.example.tellstory.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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

        //toLoginActivity(name)

        /*signUpViewModel.newRegister(name, email, pass)
        signUpViewModel.statusMessage.observe(this@RegisterActivity) { statusMessage ->
            signUpViewModel.toastMessage.observe(this@RegisterActivity) { message ->

                if (statusMessage) {
                    signUpViewModel.loading.observe(this@RegisterActivity) {
                        showLoading(it)
                    }
                } else {
                    AlertDialog.Builder(this@RegisterActivity)
                        .setTitle(R.string.register).setMessage(message)
                        .setPositiveButton("YES") { _, _ ->
                            finish()
                        }.create().show()
                }
            }
        }*/

        signUpViewModel.apply {
            newRegister(name, email, pass)
            loading.observe(this@RegisterActivity) {
                showLoading(it)
            }
            statusMessage.observe(this@RegisterActivity) { ifStatus ->
                toastMessage.observe(this@RegisterActivity) { message ->

                    if (ifStatus) {
                        loading.observe(this@RegisterActivity) { loading ->
                            showLoading(loading)
                        }
                        /*android.app.AlertDialog.Builder(this@RegisterActivity)
                            .setTitle(R.string.register).setMessage(message)
                            .setPositiveButton("YES") { _, _ ->
                                finish()
                            }.create().show()*/
                        showStatus(ifStatus)
                    } else {
                        showStatus(ifStatus)
                    }
                }
            }
        }
    }

    private fun showStatus(statusSuccess: Boolean) {
        if (statusSuccess) {
            Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Email is already taken", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingProgress.visibility = View.VISIBLE
            binding.btnregister.visibility = View.GONE
        } else {
            binding.loadingProgress.visibility = View.GONE
            binding.btnregister.visibility = View.VISIBLE
        }
    }

    private fun toLoginActivity(name: String) {
        Intent(this@RegisterActivity, LoginActivity::class.java).also {
            it.putExtra(LoginActivity.LOGIN_EXTRA, name)
            startActivity(it)
            finish()
        }
    }

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }
}