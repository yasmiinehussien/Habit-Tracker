package com.example.habit_compose.ui.theme

import android.content.Context
import android.content.res.Configuration
import java.util.*

fun updateLocale(context: Context, language: String): Context {
    val locale = Locale(language.lowercase())
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    return context.createConfigurationContext(config)
}
