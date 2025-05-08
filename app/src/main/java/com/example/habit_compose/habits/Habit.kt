package com.example.habit_compose.habits


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


data class HabitModel(
    val id: String,
    val name: String,
    val repeatFrequency: String,
    val daysSelected: String,
    val endDate: String?,
    val endHabitOn: Boolean,
    val setReminder: Boolean,
    val howOftenPerDay: Int,
    val isRegularHabit: Boolean,
    val categoryTag: String,
    val reminderTime: String?,
    val taskDate: String?,
    val completedCount: Int = 0,
    val createdDate: String = LocalDate.now().toString(),
    val userId: String
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val repeatFrequency: String,
    val daysSelected: String,
    val endDate: String?,
    val endHabitOn: Boolean,
    val setReminder: Boolean,
    val howOftenPerDay: Int,
    val isRegularHabit: Boolean,
    val categoryTag: String,
    val reminderTime: String?,
    val taskDate: String?,
    val completedCount: Int = 0,
    val createdDate: String = LocalDate.now().toString(),
    val userId:String,
    val documentId: String? =null,
)


// convert from model to entity
fun HabitModel.toEntity(): Habit {
    return Habit(
        id = 0,
        documentId = this.id, // Firestore document ID is the original string ID
        name = this.name,
        repeatFrequency = this.repeatFrequency,
        daysSelected = this.daysSelected,
        endDate = this.endDate,
        endHabitOn = this.endHabitOn,
        setReminder = this.setReminder,
        howOftenPerDay = this.howOftenPerDay,
        isRegularHabit = this.isRegularHabit,
        categoryTag = this.categoryTag,
        reminderTime = this.reminderTime,
        taskDate = this.taskDate,
        completedCount = this.completedCount,
        createdDate = this.createdDate,
        userId = this.userId
    )
}

//convert from Entity to Model
fun Habit.toHabit(): HabitModel {
    return HabitModel(
        id = this.documentId ?: this.id.toString(),
        name = this.name,
        repeatFrequency = this.repeatFrequency,
        daysSelected = this.daysSelected,
        endDate = this.endDate,
        endHabitOn = this.endHabitOn,
        setReminder = this.setReminder,
        howOftenPerDay = this.howOftenPerDay,
        isRegularHabit = this.isRegularHabit,
        categoryTag = this.categoryTag,
        reminderTime = this.reminderTime,
        taskDate = this.taskDate,
        completedCount = this.completedCount,
        createdDate = this.createdDate,
        userId = this.userId
    )
}


