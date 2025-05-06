
package com.example.habit_compose.home
import java.time.LocalDate


class CalenderData {
    val today = LocalDate.now()

    fun getWeekDates(
        selectedDate: LocalDate = today,
        daysToShow: Int = 30 //
    ): List<DayInfo> {
        val daysFromSaturday = ((selectedDate.dayOfWeek.value % 7) + 1) % 7
        val startOfCalendar = selectedDate.minusDays(daysFromSaturday.toLong())

        return (0 until daysToShow).map { offset ->
            val date = startOfCalendar.plusDays(offset.toLong())
            DayInfo(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == today
            )
        }
    }
}

