package com.example.habit_compose


import androidx.room.*

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Int): Habit


    @Query("SELECT * FROM habits WHERE daysSelected LIKE '%' || :selectedDay || '%'")
    suspend fun getHabitsBySelectedDay(selectedDay: String): List<Habit>

    @Query("SELECT * FROM habits WHERE isRegularHabit = 1")
    suspend fun getAllRegularHabits(): List<Habit>

    @Query("SELECT * FROM habits WHERE isRegularHabit = 0")
    suspend fun getAllOneTimeTasks(): List<Habit>


}
