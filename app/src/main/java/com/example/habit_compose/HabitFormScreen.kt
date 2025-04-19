package com.example.habit_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormScreen() {
    val GreenPrimary = Color(0xFF008000)
    val DarkGreen = Color(0xFF2E7D32)
    val LightGreenSurface = Color(0xFFE0F2E9)

    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("\uD83C\uDFC8") }
    var selectedColor by remember { mutableStateOf(Color.Yellow) }
    var repeatFrequency by remember { mutableStateOf("Daily") }
    var daysSelected by remember { mutableStateOf(setOf<String>()) }
    var timeOfDay by remember { mutableStateOf("Morning") }
    var endHabitOn by remember { mutableStateOf(false) }
    var setReminder by remember { mutableStateOf(false) }
    var isRegularHabit by remember { mutableStateOf(true) }

    val icons = listOf("\uD83C\uDFC8", "\uD83C\uDFC6", "\uD83C\uDF96", "\uD83C\uDFC0", "\uD83D\uDEFC", "\uD83D\uDCDA", "\uD83C\uDFB5", "\uD83D\uDCAA", "\uD83E\uDEF8", "\uD83C\uDFA8", "\uD83D\uDCDD", "\uD83D\uDCC5", "\uD83D\uDCBC", "\uD83C\uDF4E", "\uD83E\uDEA9")
    val colors = listOf(
        Color(0xFFFFF9C4), Color(0xFFFFECB3), Color(0xFFD7CCC8), Color(0xFFFFCDD2),
        Color(0xFFF8BBD0), Color(0xFFE1BEE7), Color(0xFFBBDEFB), Color(0xFFB2EBF2),
        Color(0xFFC8E6C9), Color(0xFFE6EE9C), Color(0xFFFFF176), Color(0xFFE1F5FE)
    )
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
                    .padding(20.dp)
            ) {
                Button(
                    onClick = { /* Save logic */ },
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
                                .clickable { isRegularHabit = label == "Regular Habit" }
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
            Text("Habit Name", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g., Morning Workout") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text("Icon", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            FlowRow(
                mainAxisSpacing = 12.dp,
                crossAxisSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                icons.forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedIcon == icon) GreenPrimary.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f))
                            .clickable { selectedIcon = icon },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 24.sp)
                    }
                }
            }

            Text("Color", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 2.dp,
                                color = if (selectedColor == color) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Text("Repeat", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Daily", "Weekly", "Monthly").forEach { frequency ->
                    AssistChip(
                        onClick = { repeatFrequency = frequency },
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

            Text("On these days", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
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
                                daysSelected = if (selected) daysSelected - index.toString()
                                else daysSelected + index.toString()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day, color = if (selected) Color.White else Color.Black)
                    }
                }
            }

            Text("Do it at:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Morning", "Afternoon", "Evening").forEach { time ->
                    AssistChip(
                        onClick = { timeOfDay = time },
                        label = { Text(time) },
                        shape = RoundedCornerShape(8.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (timeOfDay == time) GreenPrimary else LightGreenSurface,
                            labelColor = if (timeOfDay == time) Color.White else Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            val switchColors = SwitchDefaults.colors(
                checkedThumbColor = GreenPrimary,
                checkedTrackColor = LightGreenSurface,
                uncheckedThumbColor = DarkGreen,
                uncheckedTrackColor = DarkGreen.copy(alpha = 0.4f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = endHabitOn,
                    onCheckedChange = { endHabitOn = it },
                    colors = switchColors
                )
                Text("End Habit on", modifier = Modifier.padding(start = 8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = setReminder,
                    onCheckedChange = { setReminder = it },
                    colors = switchColors
                )
                Text("Set Reminder", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
