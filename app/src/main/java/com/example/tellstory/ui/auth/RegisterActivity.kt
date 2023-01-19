package com.example.tellstory.ui.auth

import android.content.Context
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

        signUpViewModel.apply {
            newRegister(name, email, pass)
            showLog(this.toastMessage)
        }

        //newRegis(name, email, pass)


    }

    private fun showLog(toastMessage: LiveData<String>) {
        Log.d(TAG, toastMessage.toString())

    }

    /*fun newRegis(name: String, email: String, pass: String) {
        val registerService = apiService.registerService(name, email, pass)
        //val registerService = ApiConfig.getApiService().registerService(name, email, pass)

        registerService.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {

                    if (body.message != "Email is already taken") {
                        signUpViewModel.saveNewAuth(
                            StoryUser(
                                email,
                                name,
                                "",
                                pass,
                                false
                            )
                        )
                        Log.d("registerActivityu", response.message())
                    }
                } else {
                    Log.e("registerActivity", response.message())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("registerActivity", "fail${t.message}")
                t.printStackTrace()
            }

        })

    }*/

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }
}