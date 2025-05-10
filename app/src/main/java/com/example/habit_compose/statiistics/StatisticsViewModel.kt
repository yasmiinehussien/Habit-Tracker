package com.example.habit_compose.statiistics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_compose.habits.HabitProgressDao
import com.example.habit_compose.habits.MonthlyAvgProgress
import com.example.habit_compose.habits.WeeklyAvgProgress
import com.example.habit_compose.statiistics.DailyAvgProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields

class StatisticsViewModel(
    private val dao: HabitProgressDao
) : ViewModel() {

    private val _dailyAverages = MutableStateFlow<List<DailyAvgProgress>>(emptyList())
    val dailyAverages: StateFlow<List<DailyAvgProgress>> = _dailyAverages
    private val _weeklyAverages = MutableStateFlow<List<WeeklyAvgProgress>>(emptyList())
    val weeklyAverages: StateFlow<List<WeeklyAvgProgress>> = _weeklyAverages

    private val _monthlyAverages = MutableStateFlow<List<MonthlyAvgProgress>>(emptyList())
    val monthlyAverages: StateFlow<List<MonthlyAvgProgress>> = _monthlyAverages



    //    fun loadDailyAverages() {
//        viewModelScope.launch {
//            val last7Days = getLast7Days()
//            val result = dao.getAverageProgressForDates(last7Days)
//            _dailyAverages.value = result
//        }
//    }
    fun loadDailyAverages() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }
            val dateStrings = last7Days.map { it.toString() }

            val rawData = dao.getAverageProgressForDates(dateStrings)
            val rawDataMap = rawData.associateBy { it.date }

            val filledData = dateStrings.map { date ->
                rawDataMap[date] ?: DailyAvgProgress(date, 0.0)
            }

            _dailyAverages.value = filledData
        }
    }
    fun loadWeeklyAverages() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val allProgress = dao.getAllAverageProgress()

            val progressMap = allProgress
                .map {
                    val date = LocalDate.parse(it.date, formatter)
                    val week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                    week to it.avgProgress
                }
                .groupBy { it.first }
                .mapValues { (_, list) -> list.map { it.second }.average() }

            val last4Weeks = (0..3).map { today.minusWeeks(it.toLong()).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) }.distinct().sorted()

            val filledData = last4Weeks.map { weekNumber ->
                WeeklyAvgProgress(weekNumber, progressMap[weekNumber] ?: 0.0)
            }

            _weeklyAverages.value = filledData
        }
    }


    fun loadMonthlyAverages() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val allProgress = dao.getAllAverageProgress()

            val progressMap = allProgress
                .map {
                    val date = LocalDate.parse(it.date, formatter)
                    val month = date.monthValue
                    month to it.avgProgress
                }
                .groupBy { it.first }
                .mapValues { (_, list) -> list.map { it.second }.average() }

            val last6Months = (0..5).map { today.minusMonths(it.toLong()).monthValue }.distinct().sorted()

            val filledData = last6Months.map { month ->
                MonthlyAvgProgress(month, progressMap[month] ?: 0.0)
            }

            _monthlyAverages.value = filledData
        }
    }


    suspend fun getWeeklyAverageProgress(): List<WeeklyAvgProgress> {
        val allDailyProgress = dao.getAllAverageProgress()

// نحول التواريخ إلى LocalDate ونقسمها حسب الأسبوع
        return allDailyProgress
            .map {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date = LocalDate.parse(it.date, formatter)
                val weekOfYear = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                weekOfYear to it.avgProgress
            }
            .groupBy { it.first }
            .map { (weekNumber, progresses) ->
                val avg = progresses.map { it.second }.average()
                WeeklyAvgProgress(weekNumber, avg)
            }
            .sortedBy { it.weekNumber }
    }
    suspend fun getMonthlyAverageProgress(): List<MonthlyAvgProgress> {
        val allDailyProgress = dao.getAllAverageProgress()

        return allDailyProgress
            .map {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date = LocalDate.parse(it.date, formatter)
                val month = date.monthValue
                month to it.avgProgress
            }
            .groupBy { it.first }
            .map { (month, progresses) ->
                val avg = progresses.map { it.second }.average()
                MonthlyAvgProgress(month, avg)
            }
            .sortedBy { it.month }
    }


    private fun getLast7Days(): List<String> {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return (0..6).map {
            java.time.LocalDate.now().minusDays((6 - it).toLong()).format(formatter)
        }
    }
}