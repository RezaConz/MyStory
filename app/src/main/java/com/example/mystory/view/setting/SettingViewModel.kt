package com.example.mystory.view.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystory.data.LanguagePreferences
import kotlinx.coroutines.launch
import java.util.Locale

class SettingViewModel(private val languagePreferences: LanguagePreferences) : ViewModel() {

    fun getCurrentLanguage(): LiveData<Locale> {
        return languagePreferences.getCurrentLanguage().asLiveData()
    }

    fun setCurrentLanguage(languageCode: String) {
        viewModelScope.launch {
            languagePreferences.setLanguage(languageCode)
        }
    }
}