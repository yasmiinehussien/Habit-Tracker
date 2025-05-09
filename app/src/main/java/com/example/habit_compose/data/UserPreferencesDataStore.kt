package com.example.habit_compose.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// إنشاء كائن DataStore على مستوى الملف
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * فئة مساعدة للوصول إلى وتخزين تفضيلات المستخدم باستخدام DataStore
 */
class UserPreferencesDataStore(private val context: Context) {

    // تعريف المفاتيح للتفضيلات
    companion object {
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val VACATION_MODE_ENABLED = booleanPreferencesKey("vacation_mode_enabled")
    }

    /**
     * الحصول على URI لصورة البروفايل المخزنة
     */
    val profileImageUri: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PROFILE_IMAGE_URI] ?: ""
        }

    /**
     * الحصول على حالة الوضع الداكن
     */
    val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DARK_MODE_ENABLED] ?: false
        }

    /**
     * الحصول على حالة وضع الإجازة
     */
    val isVacationModeEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[VACATION_MODE_ENABLED] ?: false
        }

    /**
     * حفظ URI لصورة البروفايل
     */
    suspend fun saveProfileImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI] = uri
        }
    }

    /**
     * تبديل حالة الوضع الداكن
     */
    suspend fun toggleDarkMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = isEnabled
        }
    }

    /**
     * تبديل حالة وضع الإجازة
     */
    suspend fun toggleVacationMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VACATION_MODE_ENABLED] = isEnabled
        }
    }
}