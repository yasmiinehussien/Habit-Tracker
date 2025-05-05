package com.example.habit_compose

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val habitProgressDao: HabitProgressDao
) {
    // Get current streak
    suspend fun getCurrentStreak(): Int {
        return habitProgressDao.getCurrentStreak()
    }

    // Get longest streak
    suspend fun getLongestStreak(): Int {
        return habitProgressDao.getLongestStreak() ?: 0
    }

    // Generate badges based on streak and vacation mode
    suspend fun getBadges(): Flow<List<Badge>> {
        return habitProgressDao.getVacationMode().map { vacationMode ->
            val currentStreak = getCurrentStreak()
            Badge.generateBadges(
                currentStreak = currentStreak,
                isVacationMode = vacationMode?.isEnabled ?: false
            )
        }
    }

    // Get vacation mode status
    fun getVacationMode(): Flow<VacationModeEntity?> {
        return habitProgressDao.getVacationMode()
    }

    // Set vacation mode
    suspend fun setVacationMode(isEnabled: Boolean) {
        val currentDate = LocalDate.now().toString()
        habitProgressDao.setVacationMode(
            VacationModeEntity(
                isEnabled = isEnabled,
                startDate = if (isEnabled) currentDate else null
            )
        )
    }

    // Get daily chart data
    suspend fun getDailyChartData(): List<ChartData> {
        val completionRates = habitProgressDao.getDailyCompletionRates()
        return completionRates.map { rate ->
            val date = LocalDate.parse(rate.date)
            ChartData(
                label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                value = rate.completion_rate,
                isToday = date == LocalDate.now()
            )
        }
    }

    // Get weekly chart data
    suspend fun getWeeklyChartData(): List<ChartData> {
        val completionRates = habitProgressDao.getWeeklyCompletionRates()
        return completionRates.mapIndexed { index, rate ->
            val date = LocalDate.parse(rate.date)
            val weekNumber = date.format(DateTimeFormatter.ofPattern("'W'w"))
            ChartData(
                label = weekNumber,
                value = rate.completion_rate,
                isToday = index == completionRates.size - 1
            )
        }
    }

    // Get monthly chart data
    suspend fun getMonthlyChartData(): List<ChartData> {
        val completionRates = habitProgressDao.getMonthlyCompletionRates()
        return completionRates.map { rate ->
            val date = LocalDate.parse(rate.date)
            ChartData(
                label = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                value = rate.completion_rate,
                isToday = date.month == LocalDate.now().month &&
                        date.year == LocalDate.now().year
            )
        }
    }
}

data class ChartData(
    val label: String,
    val value: Float,
    val isToday: Boolean = false
)