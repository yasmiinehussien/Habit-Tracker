package com.example.habit_compose.habits

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String = "",           // Firestore ID
    val userId: String = "",                   // Firebase User ID
    val name: String = "",
    val repeatFrequency: String = "",
    val daysSelected: String = "",
    val endDate: String? = null,
    val endHabitOn: Boolean = false,
    val setReminder: Boolean = false,
    val howOftenPerDay: Int = 1,
    @get:com.google.firebase.firestore.PropertyName("regularHabit")
    @set:com.google.firebase.firestore.PropertyName("regularHabit")
    var isRegularHabit: Boolean = true,


    val categoryTag: String = "",
    val reminderTime: String? = null,
    val taskDate: String? = null,
    val completedCount: Int = 0,
    val createdDate: String = LocalDate.now().toString()
)
