package com.example.habit_compose.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7E68C4),
    secondary = Color(0xFF625b71),
    background = Color(0xFFF7F7F7),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary =  Color(0xFFFFFFFF)  ,
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF625b71),
    background = Color(0xFF2A2929),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color(0xFFE1CFCF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFFFFFFF) ,
    onSurface = Color(0xFFFFFFFF)
)

@Composable
fun HabitComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Optional: For Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            window.statusBarColor = colorScheme.background.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
