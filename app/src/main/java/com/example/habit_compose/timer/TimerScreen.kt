package com.example.habit_compose.timer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_compose.PickerTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TimerScreen(onBack: () -> Unit = {}) {
    BackHandler { onBack() }
    var stopwatchViewModel: MainViewModel = viewModel()
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = stopwatch, 1 = countdownTimer

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .padding(bottom = 40.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> StopwatchScreen(stopwatchViewModel)
                1 -> CountdownScreen()
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { selectedTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
            ) {
                Text(
                    text = "Timer",
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Button(
                onClick = { selectedTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
            ) {
                Text(
                    text = "Stopwatch",
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun CountdownScreen() {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedSecond by remember { mutableStateOf(0) }
    var timerViewModel: MainViewModel = viewModel()
    var isTimerStarted by remember { mutableStateOf(false) }

    LaunchedEffect(timerViewModel) {
        timerViewModel.onTimerFinished = { isTimerStarted = false }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        if (!isTimerStarted) {
            PickerTime(
                onTimeChanged = { hour, minute, second ->
                    selectedHour = hour
                    selectedMinute = minute
                    selectedSecond = second
                }
            )

            Spacer(modifier = Modifier.height(23.dp))

            IconButton(
                onClick = {
                    val initialTime: Duration = ((selectedHour * 3600) + (selectedMinute * 60) + selectedSecond).seconds
                    val countdown = true
                    timerViewModel.start(initialTime, countdown)
                    isTimerStarted = true
                },
                modifier = Modifier
                    .size(63.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                    .padding(12.dp),
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            CountdownTimer(
                viewModel = timerViewModel,
                hour = selectedHour,
                minute = selectedMinute,
                second = selectedSecond,
                onStop = {
                    isTimerStarted = false
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TimerScreenPreview() {
    MaterialTheme {
        TimerScreen()
    }
}