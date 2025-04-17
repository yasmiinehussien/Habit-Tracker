package com.example.habittracker

import java.time.LocalDate


class CalenderData {
    val today = LocalDate.now()

    fun getWeekDates(selectedDate: LocalDate = today): List<DayInfo> {
        val daysFromSaturday =
            ((selectedDate.dayOfWeek.value % 7) + 1) % 7 // make week start from saturday=0
        val startOfWeek = selectedDate.minusDays(daysFromSaturday.toLong())


        return (0..6).map { it ->
            val date = startOfWeek.plusDays(it.toLong())
            DayInfo(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == today
            )
        }
    }
}
