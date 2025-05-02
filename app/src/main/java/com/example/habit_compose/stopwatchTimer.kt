package com.example.habit_compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habit_compose.ui.theme.HabitComposeTheme
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun StopwatchScreen(viewModel: MainViewModel) {
    val isPlaying by remember { derivedStateOf { viewModel.isPlaying } }
    val seconds by remember { derivedStateOf { viewModel.seconds } }
    val minutes by remember { derivedStateOf { viewModel.minutes } }
    val hours by remember { derivedStateOf { viewModel.hours } }

    StopwatchScreenWithBackground(
        isPlaying = isPlaying,
        seconds = seconds,
        minutes = minutes,
        hours = hours,
        onStart = { viewModel.start() },
        onPause = { viewModel.pause() },
        onStop = { viewModel.stop() }
    )
}

@Composable
fun StopwatchScreenWithBackground(
    isPlaying: Boolean,
    seconds: String,
    minutes: String,
    hours: String,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        StopwatchUI(
            isPlaying = isPlaying,
            seconds = seconds,
            minutes = minutes,
            hours = hours,
            onStart = onStart,
            onPause = onPause,
            onStop = onStop
        )
    }
}

@Composable
private fun StopwatchUI(
    isPlaying: Boolean,
    seconds: String,
    minutes: String,
    hours: String,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    var hasStarted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(23.dp))
        Text(
            text = "Challenge yourself now !",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 26.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.displaySmall) {
                AnimatedContent(targetState = hours) {
                    Text(it, color = MaterialTheme.colorScheme.onBackground)
                }
                Text(":", color = MaterialTheme.colorScheme.onBackground)
                AnimatedContent(targetState = minutes) {
                    Text(it, color = MaterialTheme.colorScheme.onBackground)
                }
                Text(":", color = MaterialTheme.colorScheme.onBackground)
                AnimatedContent(targetState = seconds) {
                    Text(it, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = isPlaying) { playing ->
                if (playing) {
                    IconButton(
                        onClick = onPause,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(50))
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Filled.Pause, contentDescription = "Pause", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                } else {
                    IconButton(
                        onClick = {
                            onStart()
                            hasStarted = true
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            if (hasStarted) {
                IconButton(
                    onClick = {
                        onStop()
                        hasStarted = false
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        .padding(top = 12.dp)
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(62.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HabitComposeTheme {
        StopwatchUI(
            isPlaying = false,
            seconds = "00",
            minutes = "00",
            hours = "00"
        )
    }
}
