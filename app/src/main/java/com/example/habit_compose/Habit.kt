package com.example.habit_compose


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val repeatFrequency: String,
    val daysSelected: String,
    val endDate: String?,
    val endHabitOn: Boolean,
    val setReminder: Boolean,
    val howOftenPerDay: Int,
    val isRegularHabit: Boolean,
    val categoryTag: String,
    val reminderTime: String?,
    val taskDate: String?,
    val completedCount: Int = 0
)
