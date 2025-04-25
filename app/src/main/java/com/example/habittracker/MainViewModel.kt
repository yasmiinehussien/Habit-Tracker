package com.example.habittracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainViewModel : ViewModel() {

    private var time: Duration = Duration.ZERO
    private lateinit var timer: Timer

    var seconds by mutableStateOf("00")
    var minutes by mutableStateOf("00")
    var hours by mutableStateOf("00")
    var isPlaying by mutableStateOf(false)

    fun start() {
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            time = time.plus(1.seconds)
            updateTimeStates()
        }
        isPlaying = true
    }

    private fun updateTimeStates() {
        time.toComponents { hours, minutes, seconds, _ ->

            this@MainViewModel.seconds = seconds.toString().padStart(2, '0')
            this@MainViewModel.minutes = minutes.toString().padStart(2, '0')
            this@MainViewModel.hours = hours.toString().padStart(2, '0')
        }
    }

    fun pause() {
        timer.cancel()
        isPlaying = false
    }

    fun stop() {
        pause()
        time = Duration.ZERO
        updateTimeStates()
    }
}
