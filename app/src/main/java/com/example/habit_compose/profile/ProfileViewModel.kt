package com.example.habit_compose.profile


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habit_compose.data.UserPreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel خاص بشاشة البروفايل
 * يتعامل مع تخزين واسترجاع بيانات المستخدم
 */
class ProfileViewModel(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    // حالات لواجهة المستخدم
    private val _profileImageUri = MutableStateFlow("")
    val profileImageUri: StateFlow<String> = _profileImageUri.asStateFlow()

    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    private val _isVacationModeEnabled = MutableStateFlow(false)
    val isVacationModeEnabled: StateFlow<Boolean> = _isVacationModeEnabled.asStateFlow()

    init {
        // جلب البيانات المخزنة عند إنشاء ViewModel
        loadUserPreferences()
    }

    /**
     * تحميل تفضيلات المستخدم المخزنة
     */
    private fun loadUserPreferences() {
        viewModelScope.launch {
            userPreferencesDataStore.profileImageUri.collect { uri ->
                _profileImageUri.value = uri
            }
        }

        viewModelScope.launch {
            userPreferencesDataStore.isDarkModeEnabled.collect { isEnabled ->
                _isDarkModeEnabled.value = isEnabled
            }
        }

        viewModelScope.launch {
            userPreferencesDataStore.isVacationModeEnabled.collect { isEnabled ->
                _isVacationModeEnabled.value = isEnabled
            }
        }
    }

    /**
     * حفظ URI الصورة الشخصية
     */
    fun saveProfileImageUri(uri: String) {
        viewModelScope.launch {
            userPreferencesDataStore.saveProfileImageUri(uri)
            _profileImageUri.value = uri
        }
    }

    /**
     * تبديل حالة الوضع الداكن
     */
    fun toggleDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesDataStore.toggleDarkMode(isEnabled)
            _isDarkModeEnabled.value = isEnabled
        }
    }

    /**
     * تبديل حالة وضع الإجازة
     */
    fun toggleVacationMode(isEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesDataStore.toggleVacationMode(isEnabled)
            _isVacationModeEnabled.value = isEnabled
        }
    }

    /**
     * مصنع لإنشاء ViewModel مع الوصول إلى Context
     */
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                val dataStore = UserPreferencesDataStore(context)
                return ProfileViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}