package com.example.habit_compose

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_progress")
data class HabitProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String, // yyyy-MM-dd
    val completedCount: Int
)
