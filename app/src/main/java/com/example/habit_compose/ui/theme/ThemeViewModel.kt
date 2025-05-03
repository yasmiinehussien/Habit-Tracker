package com.example.habit_compose.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en") // use language codes
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun setLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
    }
}
