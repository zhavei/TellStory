package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.coredata.remote.AddNewStoryRequest
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class AddNewStoryViewModel(
    private val repository: TellStoryRepository,
) : ViewModel() {

    private val _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> = _loadingStatus

    private var _statusUploaded = MutableLiveData<Boolean>()
    val statusUploaded: LiveData<Boolean> get() = _statusUploaded

    private var _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> get() = _responseMessage


    fun postNewStory(file: File, lat: Float?, lon: Float?, desc: String) {
        _loadingStatus.postValue(true)
        val requestData = AddNewStoryRequest(
            desc, lat, lon
        )
        val imageType = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("photo", file.name, imageType)

        viewModelScope.launch {
            runCatching {
                val data = repository.addNewStory(requestData, image)
                if (data) {
                    _statusUploaded.postValue(true)
                    "New Story Added Successfully"
                } else {
                    "Upload Failed"
                }
            }.onSuccess {
                _loadingStatus.postValue(false)
                _responseMessage.postValue(it)
                Log.d(TAG, "postNewStory: $it")
            }.onFailure {
                _loadingStatus.postValue(false)
                _responseMessage.postValue("${it.message}")
            }
        }
    }

    companion object {
        private val TAG = AddNewStoryViewModel::class.simpleName
    }

}