package com.example.habit_compose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // This is essential for Hilt initialization
class HabitApplication : Application()
