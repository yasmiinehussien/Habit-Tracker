
package com.example.habit_compose.statiistics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_compose.habits.HabitProgressDao
import com.example.habit_compose.statiistics.DailyAvgProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatisticsViewModel(
    private val dao: HabitProgressDao
) : ViewModel() {

    private val _dailyAverages = MutableStateFlow<List<DailyAvgProgress>>(emptyList())
    val dailyAverages: StateFlow<List<DailyAvgProgress>> = _dailyAverages



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


    private fun getLast7Days(): List<String> {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return (0..6).map {
            java.time.LocalDate.now().minusDays((6 - it).toLong()).format(formatter)
        }
    }
}
