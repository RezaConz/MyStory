package com.example.mystory.view.register
import com.example.mystory.data.Result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.remote.repository.StoryRepository
import com.example.mystory.data.remote.response.ErrorResponse

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun register(nameInput: String, emailInput: String, passwordInput: String): LiveData<Result<ErrorResponse>> =
        storyRepository.register(nameInput, emailInput, passwordInput)
}