package com.example.mystory.view.map

import androidx.lifecycle.ViewModel
import com.example.mystory.data.remote.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getAllStoriesWithLocation() = storyRepository.getStoriesWithLocation()

}