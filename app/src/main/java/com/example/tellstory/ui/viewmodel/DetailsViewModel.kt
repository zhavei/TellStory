package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.launch

class DetailsViewModel(private val tellStoryRepository: TellStoryRepository) : ViewModel() {

    private val _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> = _loadingStatus

    private val _storyDetails = MutableLiveData<MainStory>()
    val storyDetails: LiveData<MainStory> = _storyDetails

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage


    fun fetchStoryDetails(storyId: String) {
        _loadingStatus.value = true

        viewModelScope.launch {
            runCatching {
                tellStoryRepository.getStoryDetail(userId = storyId)
            }.onSuccess {
                _storyDetails.value = it
            }.onFailure {
                Log.e(TAG, "fetchStoryDetails: ${it.message}")
                _statusMessage.value = "failed to get data"
            }

            _loadingStatus.value = false
        }
    }


    companion object {
        private val TAG = DetailsViewModel::class.simpleName
    }


}