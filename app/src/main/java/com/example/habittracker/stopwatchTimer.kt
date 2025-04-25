package com.example.habittracker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.ExperimentalTime
import com.example.habittracker.R
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius

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

        Image(
            painter = painterResource(id = R.drawable.colour),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = "Challenge yourself now !",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.displaySmall) {
                AnimatedContent(targetState = hours) { Text(it, color = Color.White) }
                Text(":", color = Color.White)
                AnimatedContent(targetState = minutes) { Text(it, color = Color.White) }
                Text(":", color = Color.White)
                AnimatedContent(targetState = seconds) { Text(it, color = Color.White) }
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
                            .background(Color(0xFFBD7CA6), RoundedCornerShape(50))
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Filled.Pause, contentDescription = "Pause")
                    }
                } else {
                    IconButton(
                        onClick = {
                            onStart()
                            hasStarted = true
                        },
                        modifier = Modifier
                            .background(Color(0xFF95709D), RoundedCornerShape(50))
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
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
                        .background(Color(0xFF95709D), RoundedCornerShape(50))
                        .padding(12.dp)
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stop")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StopwatchUI(
        isPlaying = false,
        seconds = "00",
        minutes = "00",
        hours = "00"
    )
}