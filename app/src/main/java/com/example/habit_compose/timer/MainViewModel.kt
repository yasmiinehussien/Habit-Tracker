package com.example.habit_compose.timer


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    var remainingTime by mutableStateOf(0L)

    var isCountdownMode by mutableStateOf(false)
    var onTimerFinished: (() -> Unit)? = null
    fun start(initialDuration:Duration ?=null,countdown :Boolean=false) {
        isCountdownMode=countdown

        if(initialDuration!=null){
            time=initialDuration
            updateTimeStates()
        }

        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {

            if(isCountdownMode){
                time=time.minus(1.seconds)
                updateTimeStates()

                if(time <=Duration.ZERO){
                    stop()
                    isPlaying=false
                    onTimerFinished?.invoke()
                }
            }else{
                time = time.plus(1.seconds)
                updateTimeStates()

            }

        }
        isPlaying = true
    }

    private fun updateTimeStates() {
        time.toComponents { hours, minutes, seconds, _ ->

            this@MainViewModel.seconds = seconds.toString().padStart(2, '0')
            this@MainViewModel.minutes = minutes.toString().padStart(2, '0')
            this@MainViewModel.hours = hours.toString().padStart(2, '0')
            remainingTime=time.inWholeSeconds
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
    fun resume(){
        start(remainingTime.seconds, countdown = true)
    }
}