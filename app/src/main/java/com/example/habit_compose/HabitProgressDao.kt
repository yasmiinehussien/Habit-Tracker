package com.example.habit_compose

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitProgress(habitProgress: HabitProgress)

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId")
    suspend fun getHabitProgressForHabit(habitId: Int): List<HabitProgress>

    @Query("SELECT * FROM habit_progress WHERE date = :date")
    suspend fun getHabitProgressForDate(date: String): List<HabitProgress>

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getProgressForDate(habitId: Int, date: String): HabitProgress?

    // دالة لإدراج أو تحديث سجل تقدم العادة
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(habitProgress: HabitProgress)

    @Query("SELECT COUNT(DISTINCT date) FROM habit_progress WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCompletedDaysInRange(startDate: String, endDate: String): Int

    @Query("SELECT COUNT(*) FROM habit_progress WHERE date = :date")
    suspend fun getCompletedHabitsForDate(date: String): Int

    @Query("""
        WITH RECURSIVE dates(date) AS (
            SELECT :currentDate
            UNION ALL
            SELECT date(date, '-1 day')
            FROM dates
            WHERE EXISTS (
                SELECT 1 FROM habit_progress 
                WHERE habit_progress.date = date(date, '-1 day')
            )
        )
        SELECT COUNT(*) FROM dates
    """)
    suspend fun getCurrentStreak(currentDate: String = LocalDate.now().toString()): Int

    @Query("""
        WITH consecutive_days AS (
            SELECT 
                date,
                date(date, '-' || (
                    ROW_NUMBER() OVER (ORDER BY date)
                ) || ' day') AS grp
            FROM (SELECT DISTINCT date FROM habit_progress ORDER BY date)
        )
        SELECT COUNT(*) as streak_length
        FROM consecutive_days
        GROUP BY grp
        ORDER BY streak_length DESC
        LIMIT 1
    """)
    suspend fun getLongestStreak(): Int?

    @Query("""
        WITH RECURSIVE nums(num) AS (
            SELECT 0
            UNION ALL
            SELECT num + 1 FROM nums WHERE num < :daysBack
        ),
        days AS (
            SELECT date(date('now', '-' || (:daysBack - nums.num) || ' day')) AS date
            FROM nums
        ),
        completions AS (
            SELECT 
                hp.date, 
                COUNT(*) as completed_count,
                (SELECT COUNT(*) FROM habits WHERE isRegularHabit = 1) as total_habits
            FROM habit_progress hp
            GROUP BY hp.date
        )
        SELECT 
            days.date as date,
            IFNULL(CAST(completed_count AS FLOAT) / 
                  CASE WHEN total_habits = 0 THEN 1 ELSE total_habits END, 0.0) as completion_rate
        FROM days
        LEFT JOIN completions ON days.date = completions.date
        ORDER BY days.date
    """)
    suspend fun getDailyCompletionRates(daysBack: Int = 7): List<CompletionRate>

    @Query("""
        WITH RECURSIVE nums(num) AS (
            SELECT 0
            UNION ALL
            SELECT num + 1 FROM nums WHERE num < :weeksBack
        ),
        weeks AS (
            SELECT 
                date(date('now', 'weekday 0', '-' || ((:weeksBack - nums.num) * 7) || ' days')) AS week_start,
                date(date('now', 'weekday 0', '-' || ((:weeksBack - nums.num) * 7) || ' days'), '+6 days') AS week_end
            FROM nums
        ),
        week_completions AS (
            SELECT 
                strftime('%W', hp.date) as week_num,
                COUNT(DISTINCT hp.date) as completed_days
            FROM habit_progress hp
            WHERE hp.date >= (SELECT MIN(week_start) FROM weeks)
            GROUP BY week_num
        )
        SELECT 
            weeks.week_start as date,
            IFNULL(CAST(completed_days AS FLOAT) / 7.0, 0.0) as completion_rate
        FROM weeks
        LEFT JOIN week_completions ON strftime('%W', weeks.week_start) = week_completions.week_num
        ORDER BY weeks.week_start
    """)
    suspend fun getWeeklyCompletionRates(weeksBack: Int = 5): List<CompletionRate>

    @Query("""
        WITH RECURSIVE nums(num) AS (
            SELECT 0
            UNION ALL
            SELECT num + 1 FROM nums WHERE num < :monthsBack
        ),
        months AS (
            SELECT 
                date(date('now', 'start of month', '-' || (:monthsBack - nums.num) || ' months')) AS month_start,
                date(date('now', 'start of month', '-' || (:monthsBack - nums.num - 1) || ' months', '-1 day')) AS month_end
            FROM nums
        ),
        month_completions AS (
            SELECT 
                strftime('%Y-%m', hp.date) as month,
                COUNT(DISTINCT hp.date) as completed_days,
                (julianday(date(strftime('%Y-%m', hp.date), '+1 month')) - 
                 julianday(date(strftime('%Y-%m', hp.date)))) as days_in_month
            FROM habit_progress hp
            WHERE hp.date >= (SELECT MIN(month_start) FROM months)
            GROUP BY month
        )
        SELECT 
            months.month_start as date,
            IFNULL(CAST(completed_days AS FLOAT) / days_in_month, 0.0) as completion_rate
        FROM months
        LEFT JOIN month_completions ON strftime('%Y-%m', months.month_start) = month_completions.month
        ORDER BY months.month_start
    """)
    suspend fun getMonthlyCompletionRates(monthsBack: Int = 6): List<CompletionRate>

    @Query("SELECT * FROM vacation_mode WHERE id = 1")
    fun getVacationMode(): Flow<VacationModeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setVacationMode(vacationMode: VacationModeEntity)
}

data class CompletionRate(
    val date: String,
    val completion_rate: Float
)
