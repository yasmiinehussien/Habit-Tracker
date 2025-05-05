package com.example.habit_compose

import androidx.compose.ui.graphics.Color

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val color: Color,
    val isEarned: Boolean = false
) {
    companion object {
        fun generateBadges(currentStreak: Int, isVacationMode: Boolean): List<Badge> {
            return listOf(
                Badge(
                    id = "starter",
                    name = "Starter",
                    description = "Complete habits for 3 days in a row",
                    iconName = "star",
                    color = Color(0xFF6200EA),
                    isEarned = currentStreak >= 3
                ),
                Badge(
                    id = "consistent",
                    name = "Consistent",
                    description = "Complete habits for 7 days in a row",
                    iconName = "trending_up",
                    color = Color(0xFF00C853),
                    isEarned = currentStreak >= 7
                ),
                Badge(
                    id = "dedicated",
                    name = "Dedicated",
                    description = "Complete habits for 14 days in a row",
                    iconName = "local_fire_department",
                    color = Color(0xFFFF6F00),
                    isEarned = currentStreak >= 14
                ),
                Badge(
                    id = "master",
                    name = "Master",
                    description = "Complete habits for 30 days in a row",
                    iconName = "emoji_events",
                    color = Color(0xFFFFD700),
                    isEarned = currentStreak >= 30
                ),
                Badge(
                    id = "vacation",
                    name = "On Vacation",
                    description = "You're taking a break from your habits",
                    iconName = "beach_access",
                    color = Color(0xFF03A9F4),
                    isEarned = isVacationMode
                )
            )
        }
    }
}