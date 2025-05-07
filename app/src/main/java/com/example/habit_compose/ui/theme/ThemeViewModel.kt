package com.example.habit_compose.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // Dark theme state (persistent)
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Language state (not persisted)
    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        // Initialize theme state by collecting preferences from DataStore
        viewModelScope.launch {
            ThemePreference.getDarkModePreference(context).collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        _isDarkTheme.value = isDark
        // Save to DataStore
        viewModelScope.launch {
            ThemePreference.saveDarkModePreference(context, isDark)
        }
    }

    fun setLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
    }
}
