package com.example.habit_compose

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacation_mode")
data class VacationModeEntity(
    @PrimaryKey val id: Int = 1, // Single row for app settings
    val isEnabled: Boolean = false,
    val startDate: String? = null // Store the date when vacation mode was enabled
)