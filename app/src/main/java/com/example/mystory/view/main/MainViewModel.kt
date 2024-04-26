package com.example.mystory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.mystory.data.LoginPreferences
import com.example.mystory.data.remote.repository.StoryRepository
import com.example.mystory.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch
import com.example.mystory.data.Result

class MainViewModel(
    storyRepository: StoryRepository,
    private val preferences: LoginPreferences
) : ViewModel() {

    val getStories: LiveData <Result<PagingData<ListStoryItem>>> by lazy {
        storyRepository.getAllStoriesWithPaging(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            preferences.logout()
        }
    }
}