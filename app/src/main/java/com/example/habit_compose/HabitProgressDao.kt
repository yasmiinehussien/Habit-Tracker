package com.example.habit_compose

import androidx.room.*

@Dao
interface HabitProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: HabitProgress)

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId AND date = :date")
    suspend fun getProgressForDate(habitId: Int, date: String): HabitProgress?

    @Query("DELETE FROM habit_progress WHERE habitId = :habitId")
    suspend fun deleteAllProgressForHabit(habitId: Int)
}
