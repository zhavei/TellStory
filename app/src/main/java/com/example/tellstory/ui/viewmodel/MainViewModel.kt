package com.example.tellstory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val tellStoryRepository: TellStoryRepository) : ViewModel() {

    //get the paging data
    val listStories: LiveData<PagingData<MainStory>> =
        tellStoryRepository.fetchAllStories().cachedIn(viewModelScope)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _welcomeUser = MutableLiveData<String>()
    val welcomeUser: LiveData<String> = _welcomeUser

    fun getTheName() {
        viewModelScope.launch {
            //get the username
            val user = tellStoryRepository.observeUserName()
            _welcomeUser.value = user.toString()
            Log.d("Main viewModel", user.toString())
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}