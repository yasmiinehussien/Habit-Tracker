package com.example.habit_compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==== LIGHT COLOR SCHEME ====
private val LightColors = lightColorScheme(
    primary = Color(0xFF9472EA),          // البنفسجي الأساسي
    onPrimary = Color.White,              // النص فوق اللون الأساسي

    secondary = Color(0xFFD4C2F6),        // بنفسجي فاتح للزر الثانوي أو الهامش
    onSecondary = Color(0xFF2E2C3A),      // نص عليه

    tertiary = Color(0xFF6C4DB3),         // تمييز (للشارات أو التقدم)
    onTertiary = Color.White,

    background = Color(0xFFF9F8FF),       // خلفية ناعمة مائلة للبنفسجي
    onBackground = Color(0xFF1E1B29),     // نص غامق مريح

    surface = Color(0xFFFFFFFF),          // بطاقات، مناطق عمل
    onSurface = Color(0xFF2E2C3A),        // تباين جيد

    error = Color(0xFFD32F2F),            // أخطاء
    onError = Color.White,
)

// ==== DARK COLOR SCHEME ====
private val DarkColors = darkColorScheme(
    primary = Color(0xFFBFA5F8),          // نفس اللون الأساسي لكن مضيء
    onPrimary = Color.Black,

    secondary = Color(0xFF9472EA),
    onSecondary = Color.White,

    tertiary = Color(0xFF6C4DB3),
    onTertiary = Color.White,

    background = Color(0xFF121016),       // داكن جدًا
    onBackground = Color(0xFFEDE6FF),     // نص فاتح بنفسجي خفيف

    surface = Color(0xFF1E1B29),          // بطاقات داكنة
    onSurface = Color(0xFFE3DEF5),

    error = Color(0xFFEF9A9A),
    onError = Color.Black,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
