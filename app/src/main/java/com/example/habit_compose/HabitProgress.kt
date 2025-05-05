package com.example.habit_compose

import androidx.room.Entity

@Entity(tableName = "habit_progress", primaryKeys = ["habitId", "date"])
data class HabitProgress(
    val habitId: Int,
    val date: String, // yyyy-MM-dd
    val completedCount: Int
)
