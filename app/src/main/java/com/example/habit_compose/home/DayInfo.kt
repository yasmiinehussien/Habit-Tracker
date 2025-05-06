package com.example.habit_compose.home


import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DayInfo(
    val date: LocalDate,// the real date
    val isSelected: Boolean,
    val isToday: Boolean
) {
    val day: String = date.format(DateTimeFormatter.ofPattern("E")) //gives short name of the day
}