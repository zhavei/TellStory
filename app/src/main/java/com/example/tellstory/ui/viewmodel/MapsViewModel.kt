package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.repository.TellStoryRepository
import com.example.tellstory.ui.maps.MapsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel(private val tellStoryRepository: TellStoryRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _mapsStory = MutableLiveData<List<MainStory>>()
    val mapsStory: LiveData<List<MainStory>> get() = _mapsStory

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
    }

    fun getMapsStories() {
        _loading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val mapsStories = withContext(Dispatchers.IO) { tellStoryRepository.getMapsStories() }
                _mapsStory.value = mapsStories
            } catch (e: Throwable) {
                Log.i(TAG, "getMapsStories: ${e.message}")
            } finally {
                _loading.value = false
            }
        }

    }

}