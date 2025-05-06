package com.example.habit_compose.habits
import android.app.TimePickerDialog
import android.widget.Toast

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
fun HabitFormScreen(navController: NavController, categoryTag: String)
{
    val GreenPrimary = Color(0xFF7A49D5)
    val DarkGreen = Color(0xFF663AB6)
    val LightGreenSurface = Color(0xFFE0F2E9)

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
            .background(Color.White)
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
                    }
                    ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Save", fontSize = 18.sp, color = Color.White)
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
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Tabs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreenSurface)
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
                                .background(if (selected) GreenPrimary else Color.Transparent)
                                .clickable {
                                    isRegularHabit = label == "Regular Habit"
                                    name = "" // Clear the name field
                                    daysSelected = emptySet() // Clear selected days
                                    howOftenPerDay = 1 // Reset how often
                                    endDate = null
                                    endHabitOn = false
                                    reminderTime = null
                                    setReminder=false



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
                                color = if (selected) Color.White else Color.DarkGray,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(if (isRegularHabit) "Habit Name" else "Task Name", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g., Morning Workout") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )


            if (isRegularHabit) {
                Text("Repeat", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
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
                                containerColor = if (repeatFrequency == frequency) GreenPrimary else LightGreenSurface,
                                labelColor = if (repeatFrequency == frequency) Color.White else Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }



            if (showDaysSelection) {
                Text(if (isRegularHabit) "on these Days" else "on Day", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    days.forEachIndexed { index, day ->
                        val selected = daysSelected.contains(index.toString())
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) GreenPrimary else LightGreenSurface)
                                .clickable {
                                    if (isRegularHabit) {
                                        // allow multi-select for Regular
                                        daysSelected = if (selected) daysSelected - index.toString()
                                        else daysSelected + index.toString()
                                    } else {
                                        // only allow 1 day for One-Time Task
                                        daysSelected = setOf(index.toString())
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(day, color = if (selected) Color.White else Color.Black)
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("How often per day?", style = MaterialTheme.typography.labelMedium)

            var expanded by remember { mutableStateOf(false) }
            val options = listOf(1, 2, 3, 4, 5)

            Box(modifier = Modifier
                .fillMaxWidth()
                .background(LightGreenSurface, RoundedCornerShape(12.dp))
                .border(1.dp, GreenPrimary, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$howOftenPerDay times",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = GreenPrimary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .border(1.dp, GreenPrimary, RoundedCornerShape(12.dp))
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("$option times", color = Color.Black) },
                            onClick = {
                                howOftenPerDay = option
                                expanded = false
                            }
                        )
                    }
                }
            }





            Spacer(modifier = Modifier.height(24.dp))
            val switchColors = SwitchDefaults.colors(
                checkedThumbColor = GreenPrimary,
                checkedTrackColor = LightGreenSurface,
                uncheckedThumbColor = DarkGreen,
                uncheckedTrackColor = DarkGreen.copy(alpha = 0.4f)
            )

            if (isRegularHabit) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = endHabitOn,
                        onCheckedChange = { isChecked ->
                            endHabitOn = isChecked
                            if (isChecked) {
                                showDatePicker = true // open calendar
                            } else {
                                endDate = null // if user turns off, remove selected date
                            }
                        },
                        colors = switchColors
                    )

                    Text("End Habit on", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = setReminder,
                    onCheckedChange = { isChecked ->
                        setReminder = isChecked
                        if (isChecked) {
                            showTimePicker = true // open time picker
                        } else {
                            reminderTime = null // clear if user disables
                        }
                    },
                    colors = switchColors
                )

                Text("Set Reminder", modifier = Modifier.padding(start = 8.dp))
            }

// Show TimePicker if needed
            if (showTimePicker) {
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val now = java.util.Calendar.getInstance()
                    val dialog = TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val isPM = hourOfDay >= 12
                            val hourFormatted = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                            val amPm = if (isPM) "PM" else "AM"
                            reminderTime = String.format("%02d:%02d %s", hourFormatted, minute, amPm)
                            showTimePicker = false
                        },
                        now.get(java.util.Calendar.HOUR_OF_DAY),
                        now.get(java.util.Calendar.MINUTE),
                        true
                    )

                    dialog.setOnCancelListener {
                        showTimePicker = false
                        setReminder = false //
                    }

                    dialog.show()
                }
            }



//  Show the selected reminder time if set
            if (reminderTime != null) {
                Text(
                    text = "Reminder Time: $reminderTime",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )
            }


// DatePickerDialog must be OUTSIDE Row
            if (showDatePicker) {
                val today = remember { java.time.LocalDate.now() }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = System.currentTimeMillis()
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()

                                if (!selectedDate.isBefore(today)) {
                                    endDate = selectedDate.toString()
                                } else {

                                    Toast.makeText(
                                        context,
                                        "Please select a future date only.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    endHabitOn=false
                                }
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    }
                    ,
                    dismissButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            endHabitOn = false //
                        }) {
                            Text("Cancel")
                        }
                    }

                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (endDate != null) {
                val formattedDate = try {
                    LocalDate.parse(endDate).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                } catch (e: Exception) {
                    endDate // fallback to raw if parsing failed
                }

                Text(
                    text = "Selected End Date: $formattedDate",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )
            }







            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
