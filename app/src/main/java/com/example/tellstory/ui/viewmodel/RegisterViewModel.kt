package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterViewModel (
    private val tellStoryRepository: TellStoryRepository
) : ViewModel() {

    private var _registerStatus = MutableLiveData<Boolean>()
    val registerStatus: LiveData<Boolean> get() = _registerStatus

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _showError = MutableLiveData<Boolean>()
    val showError: LiveData<Boolean> get() = _showError

    fun newRegister(name: String, email: String, pass: String) {

        _loading.postValue(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val registerResult = tellStoryRepository.register(name, email, pass)
                if (registerResult) {
                    _registerStatus.postValue(true)
                    Log.d(TAG, "newRegister: ${registerResult.toString()}")
                } else {
                    _registerStatus.postValue(false)
                    _showError.postValue(true)
                    Log.d(TAG, "newRegister: ${registerResult.toString()}")
                }
            } catch (e: Throwable) {
                _showError.postValue(true)
            } finally {
                _loading.postValue(false)
            }
        }

    }

    companion object {
        private val TAG = RegisterViewModel::class.simpleName
    }
}