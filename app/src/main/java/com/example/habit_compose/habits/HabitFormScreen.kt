package com.example.habit_compose.habits
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.habit_compose.notification.scheduleHabitNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormScreen(navController: NavController, categoryTag: String) {
    var showTimePicker by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var showDaysSelection by remember { mutableStateOf(false) }
    var howOftenPerDay by remember { mutableStateOf(1) }
    var endDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var repeatFrequency by remember { mutableStateOf("Daily") }
    var daysSelected by remember { mutableStateOf(setOf<String>()) }
    var endHabitOn by remember { mutableStateOf(false) }
    var setReminder by remember { mutableStateOf(false) }
    var isRegularHabit by remember { mutableStateOf(true) }
    var taskDate by remember { mutableStateOf<String?>(null) }
    val days = listOf("S", "M", "T", "W", "T", "F", "S")

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        topBar = {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val existingNames = if (isRegularHabit) {
                                db.habitDao().getAllRegularHabits().map { it.name.trim().lowercase() }
                            } else {
                                db.habitDao().getAllOneTimeTasks().map { it.name.trim().lowercase() }
                            }

                            withContext(Dispatchers.Main) {
                                if (name.trim().isEmpty()) {
                                    Toast.makeText(context, "Please enter habit or task name!", Toast.LENGTH_SHORT).show()
                                    return@withContext
                                }
                                if (name.trim().lowercase() in existingNames) {
                                    Toast.makeText(context, "This ${if (isRegularHabit) "habit" else "task"} name already exists! Please choose another.", Toast.LENGTH_SHORT).show()
                                    return@withContext
                                }
                                if (repeatFrequency == "Weekly" && daysSelected.isEmpty()) {
                                    Toast.makeText(context, "Please select at least one day for weekly habits!", Toast.LENGTH_SHORT).show()
                                    return@withContext
                                }
                                if (!isRegularHabit && daysSelected.size != 1) {
                                    Toast.makeText(context, "Please select exactly one day for the one-time task!", Toast.LENGTH_SHORT).show()
                                    return@withContext
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    db.habitDao().insertHabit(
                                        Habit(
                                            name = name.trim(),
                                            repeatFrequency = repeatFrequency,
                                            daysSelected = daysSelected.joinToString(","),
                                            endDate = endDate,
                                            endHabitOn = endHabitOn,
                                            setReminder = setReminder,
                                            isRegularHabit = isRegularHabit,
                                            categoryTag = categoryTag,
                                            howOftenPerDay = howOftenPerDay,
                                            reminderTime = reminderTime,
                                            taskDate = if (!isRegularHabit && daysSelected.size == 1) {
                                                val selectedDayIndex = daysSelected.first().toInt()
                                                val today = LocalDate.now()
                                                val todayIndex = today.dayOfWeek.value % 7
                                                val daysUntil = (selectedDayIndex - todayIndex + 7) % 7
                                                val realDate = today.plusDays(daysUntil.toLong())
                                                realDate.toString()
                                            } else null,
                                            createdDate = LocalDate.now().toString() // ✅ Important fix
                                        )
                                    )


                                    withContext(Dispatchers.Main) {
                                        if (setReminder && reminderTime != null) {
                                            scheduleHabitNotification(context, name, reminderTime!!, daysSelected.joinToString(","))
                                        }
                                        delay(300)
                                        navController.navigate("tabs") {
                                            popUpTo("tabs") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create New Habit",
                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Tabs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    listOf("Regular Habit", "One-Time Task").forEach { label ->
                        val selected = (label == "Regular Habit") == isRegularHabit
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable {
                                    isRegularHabit = label == "Regular Habit"
                                    name = "" // Clear the name field
                                    daysSelected = emptySet() // Clear selected days
                                    howOftenPerDay = 1 // Reset how often
                                    endDate = null
                                    endHabitOn = false
                                    reminderTime = null
                                    setReminder = false

                                    if (isRegularHabit) {
                                        repeatFrequency = "Daily"
                                        showDaysSelection = false
                                        endHabitOn = false
                                    } else {
                                        repeatFrequency = ""
                                        showDaysSelection = true
                                        // endHabitOn = false
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(if (isRegularHabit) "Habit Name" else "Task Name", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g., Morning Workout", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (isRegularHabit) {
                Text("Repeat", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground), modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Daily", "Weekly").forEach { frequency ->
                        AssistChip(
                            onClick = {
                                repeatFrequency = frequency
                                showDaysSelection = frequency != "Daily"

                                if (frequency == "Daily") {
                                    daysSelected = setOf("0", "1", "2", "3", "4", "5", "6")
                                }
                            },
                            label = { Text(frequency) },
                            shape = RoundedCornerShape(8.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (repeatFrequency == frequency) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (repeatFrequency == frequency) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (showDaysSelection) {
                Text(if (repeatFrequency == "Weekly") "Select Days" else "How Often", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground), modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    days.forEachIndexed { index, day ->
                        val selected = daysSelected.contains(day)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable {
                                    if (selected) {
                                        daysSelected = daysSelected - day
                                    } else {
                                        daysSelected = daysSelected + day
                                    }
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // One-Time Task
            if (!isRegularHabit) {
                Text("One-Time Task", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground), modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                OutlinedTextField(
                    value = taskDate ?: "",
                    onValueChange = { taskDate = it },
                    label = { Text("Task Date", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}