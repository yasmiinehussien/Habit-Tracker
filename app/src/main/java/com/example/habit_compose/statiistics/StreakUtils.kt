package com.example.habit_compose.statiistics


import android.content.Context
import com.example.habit_compose.data.UserPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * أدوات مساعدة لحساب تتابع الإنجازات (Streak) مع مراعاة وضع الإجازة
 */
object StreakUtils {

    /**
     * التحقق مما إذا كان وضع الإجازة مفعل
     */
    fun isVacationModeEnabled(context: Context): Boolean {
        val userPreferences = UserPreferencesDataStore(context)
        return runBlocking { userPreferences.isVacationModeEnabled.first() }
    }

    /**
     * حساب تتابع الإنجازات مع مراعاة وضع الإجازة
     * إذا كان وضع الإجازة مفعل، سيتم تجاهل فترة الإجازة عند حساب التتابع
     */
    fun calculateStreak(
        context: Context,
        completedDates: List<String>,
        currentDate: String
    ): Int {
        // التحقق من وضع الإجازة
        val isVacationMode = isVacationModeEnabled(context)

        // في حالة تفعيل وضع الإجازة، نعيد التتابع السابق أو قيمة افتراضية
        if (isVacationMode) {
            // هنا يمكن تنفيذ منطق مخصص للحفاظ على التتابع أثناء الإجازة
            return completedDates.size
        }

        // في حالة عدم تفعيل وضع الإجازة، نقوم بالحساب العادي للتتابع
        // هنا يمكن إضافة منطق حساب التتابع الخاص بك
        // ...

        return calculateNormalStreak(completedDates, currentDate)
    }

    /**
     * حساب التتابع بالطريقة العادية (بدون وضع الإجازة)
     */
    private fun calculateNormalStreak(completedDates: List<String>, currentDate: String): Int {
        // هنا يمكنك تنفيذ منطق حساب التتابع الخاص بالتطبيق
        // هذا مجرد مثال بسيط
        return completedDates.size
    }
}