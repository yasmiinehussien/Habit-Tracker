package com.example.habit_compose.habits


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveHabit(habit: HabitModel): String {
        val userId = auth.currentUser?.uid?:throw Exception("User not authenticated")

        val habitData = hashMapOf(
            "userId" to userId,
            "name" to habit.name,
            "repeatFrequency" to habit.repeatFrequency,
            "daysSelected" to habit.daysSelected,
            "endDate" to habit.endDate,
            "endHabitOn" to habit.endHabitOn,
            "setReminder" to habit.setReminder,
            "howOftenPerDay" to habit.howOftenPerDay,
            "isRegularHabit" to habit.isRegularHabit,
            "categoryTag" to habit.categoryTag,
            "reminderTime" to habit.reminderTime,
            "taskDate" to habit.taskDate,
            "completedCount" to habit.completedCount,
            "createdDate" to habit.createdDate
        )

        val docRef = db.collection("users").document(userId)
            .collection("habits")
            .add(habitData)
            .await()

        return docRef.id
    }

    suspend fun getHabitsForUser(): List<HabitModel> {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

        val snapshot = db.collection("users").document(userId)
            .collection("habits")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            HabitModel(
                id = doc.id,
                name = doc.getString("name") ?: "",
                repeatFrequency = doc.getString("repeatFrequency") ?: "Daily",
                daysSelected = doc.getString("daysSelected") ?: "",
                endDate = doc.getString("endDate"),
                endHabitOn = doc.getBoolean("endHabitOn") ?: false,
                setReminder = doc.getBoolean("setReminder") ?: false,
                howOftenPerDay = doc.getLong("howOftenPerDay")?.toInt() ?: 1,
                isRegularHabit = doc.getBoolean("isRegularHabit") ?: true,
                categoryTag = doc.getString("categoryTag") ?: "",
                reminderTime = doc.getString("reminderTime"),
                taskDate = doc.getString("taskDate"),
                completedCount = doc.getLong("completedCount")?.toInt() ?: 0,
                createdDate = doc.getString("createdDate") ?: "",
                userId = userId
            )
        }
    }

    suspend fun deleteHabit(habitId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

        db.collection("users").document(userId)
            .collection("habits").document(habitId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document $habitId deleted")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting document", e)
                throw e
            }
            .await()
    }

}