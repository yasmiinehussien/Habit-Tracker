package com.example.habit_compose.habits

import androidx.room.*
import com.example.habit_compose.statiistics.DailyAvgProgress

@Dao
interface HabitProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: HabitProgress)

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId AND date = :date")
    suspend fun getProgressForDate(habitId: Int, date: String): HabitProgress?

    @Query("DELETE FROM habit_progress WHERE habitId = :habitId")
    suspend fun deleteAllProgressForHabit(habitId: Int)

    @Query("""
        SELECT 
            hp.date AS date,
            SUM(hp.completedCount * 1.0) / SUM(h.howOftenPerDay) * 100 AS avgProgress
        FROM habit_progress hp
        INNER JOIN habits h ON hp.habitId = h.id
        WHERE hp.date IN (:dates)
        GROUP BY hp.date
        ORDER BY hp.date ASC
    """)
    suspend fun getAverageProgressForDates(dates: List<String>): List<DailyAvgProgress>

    @Query("""
        SELECT
            hp.date AS date,
         COALESCE(SUM(hp.completedCount * 1.0) / NULLIF(SUM(h.howOftenPerDay), 0), 0) * 100 AS avgProgress

        FROM habit_progress hp
        INNER JOIN habits h ON hp.habitId = h.id
        GROUP BY hp.date
        ORDER BY hp.date ASC
    """)
    suspend fun getAllAverageProgress(): List<DailyAvgProgress>
}

