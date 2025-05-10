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


            val dailyProgressMap = allProgress
                .groupBy { it.date }
                .mapValues { (_, progressesInDay) ->
                    progressesInDay.map { it.avgProgress }.average()
                }


            val last4WeeksStartDates = (0..3).map {
                today.minusWeeks(it.toLong()).with(java.time.DayOfWeek.MONDAY)
            }


            val weeklyAverages = last4WeeksStartDates.map { weekStart ->
                val datesOfWeek = (0..6).map { weekStart.plusDays(it.toLong()) }
                val dailyValues = datesOfWeek.map { date ->
                    val dateStr = date.format(formatter)
                    dailyProgressMap[dateStr] ?: 0.0
                }
                val avgOfWeek = dailyValues.average()
                val weekNumber = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                WeeklyAvgProgress(weekNumber, avgOfWeek)
            }.sortedBy { it.weekNumber }

            _weeklyAverages.value = weeklyAverages
        }
    }


    fun loadMonthlyAverages() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val allProgress = dao.getAllAverageProgress()


            val dailyProgressMap = allProgress
                .groupBy { it.date }
                .mapValues { (_, progressesInDay) ->
                    progressesInDay.map { it.avgProgress }.average()
                }

            // 2. نجيب آخر 6 شهور كتاريخ بداية الشهر
            val last6MonthsStartDates = (0..5).map {
                today.minusMonths(it.toLong()).withDayOfMonth(1)
            }


            val monthlyAverages = last6MonthsStartDates.map { monthStart ->
                val daysInMonth = monthStart.lengthOfMonth()
                val datesOfMonth = (1..daysInMonth).map { day ->
                    monthStart.withDayOfMonth(day)
                }
                val dailyValues = datesOfMonth.map { date ->
                    val dateStr = date.format(formatter)
                    dailyProgressMap[dateStr] ?: 0.0
                }

                val avgOfMonth = dailyValues.average()
                val monthNumber = monthStart.monthValue

                MonthlyAvgProgress(monthNumber, avgOfMonth)
            }.sortedBy { it.month }

            _monthlyAverages.value = monthlyAverages
        }
    }



    suspend fun getWeeklyAverageProgress(): List<WeeklyAvgProgress> {
        val allDailyProgress = dao.getAllAverageProgress()


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