package com.example.habit_compose.habits

import androidx.room.*

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: String): Habit

    @Query("SELECT * FROM habits WHERE ',' || daysSelected || ',' LIKE '%,' || :selectedDay || ',%'")
    suspend fun getHabitsBySelectedDay(selectedDay: String): List<Habit>

    @Query("SELECT * FROM habits WHERE isRegularHabit = 1")
    suspend fun getAllRegularHabits(): List<Habit>

    @Query("SELECT * FROM habits WHERE isRegularHabit = 0")
    suspend fun getAllOneTimeTasks(): List<Habit>

    @Query("SELECT * FROM habits WHERE isRegularHabit = 0 AND taskDate = :selectedDate")
    suspend fun getTasksBySelectedDate(selectedDate: String): List<Habit>

    @Query("SELECT * FROM habits WHERE isRegularHabit = 0 AND date(taskDate) = :selectedDate")
    suspend fun getOneTimeTasksByDate(selectedDate: String): List<Habit>

    @Query("UPDATE habits SET completedCount = :count WHERE id = :habitId")
    suspend fun updateCompletedCount(habitId: String, count: Int)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: String)

    @Query("DELETE FROM habit_progress WHERE habitId = :habitId")
    suspend fun deleteHabitProgressByHabitId(habitId: String)

    @Transaction
    suspend fun deleteHabitCompletely(habitId: String) {
        deleteHabitProgressByHabitId(habitId)
        deleteHabitById(habitId)
    }
}
