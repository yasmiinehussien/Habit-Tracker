package com.example.habit_compose


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val repeatFrequency: String,
    val daysSelected: String,
    val timeOfDay: String,
    val endHabitOn: Boolean,
    val setReminder: Boolean,
    val isRegularHabit: Boolean,
    val categoryTag: String
)
