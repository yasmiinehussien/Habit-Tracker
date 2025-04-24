package com.example.habit_compose


import androidx.room.*

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>
}
