package com.example.habit_compose.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This must be at top-level (not inside an object)
private val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePreference {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    // Save dark mode preference
    suspend fun saveDarkModePreference(context: Context, isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    // Get current dark mode preference
    fun getDarkModePreference(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }
    }
}
