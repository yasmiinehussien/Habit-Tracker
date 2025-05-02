package com.example.habit_compose


import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
fun CountdownTimer(viewModel: MainViewModel
                   , hour: Int, minute: Int, second: Int
                   ,onStop: () -> Unit)
{
    val totalSeconds = (hour * 3600) + (minute * 60) + second

    val progress = remember(viewModel.seconds, viewModel.minutes, viewModel.hours) {
        val remainingSeconds = (viewModel.hours.toInt() * 3600) +
                (viewModel.minutes.toInt() * 60) +
                (viewModel.seconds.toInt())
        if (totalSeconds == 0) 0f else remainingSeconds / totalSeconds.toFloat()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp),
        ){

            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(200.dp),
                strokeWidth = 8.dp,
                color = Color(0xFF943CFD)
            )

            Text(
                text = "${viewModel.hours}:${viewModel.minutes}:${viewModel.seconds}",
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Button(

                onClick = {
                    if (!viewModel.isPlaying) {
                        viewModel.resume()
                    } else {
                        viewModel.pause()
                    }

                }

            ) {
                Text(if (viewModel.isPlaying) "Pause" else "Resume")
            }

            Button(
                onClick = {
                    viewModel.stop()
                    onStop()
                }
            ) {
                Text("Stop")
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CountdownTimerPreview() {

}
