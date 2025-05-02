package com.example.habit_compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import java.time.LocalDate


@Composable
fun NavScreen(modifier: Modifier = Modifier) {

    val navIconList = listOf(
        NavIcon("Home", Icons.Default.Home),
        NavIcon("timer", Icons.Default.Timer),
        NavIcon("Add", Icons.Default.AddCircle),
        NavIcon("Stats", Icons.Default.InsertChart),
        NavIcon("Personal", Icons.Default.Person)
    )

    val navController = rememberNavController()


    var selectedIndex by remember { mutableStateOf(0) }


    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 100.dp else 90.dp)
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 16.dp)
            ) {
                AnimatedNavigationBar(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    selectedIndex = selectedIndex,
                    cornerRadius = shapeCornerRadius(cornerRadius = 24.dp),
                    ballAnimation = Parabolic(

                        tween(durationMillis = 500)
                    ),
                    indentAnimation = Height(tween(durationMillis = 300)),
                    barColor = MaterialTheme.colorScheme.surface,
                    ballColor = Color(0xFF7852CC)
                ) {

                    navIconList.forEachIndexed { index, item ->
                        val isAddIcon = item.label == "Add"

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { selectedIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            val iconTint = when {
                                selectedIndex == index -> Color(0xFF2196F3) // مختارة
                                isAddIcon -> Color(0xFF943CFD) // لون مميز للـ Add
                                else -> Color.Gray
                            }

                            Icon(
                                modifier = Modifier
                                    .size(if (isAddIcon) 50.dp else if (isTablet) 30.dp else 24.dp)
                                    .offset(y = if (isAddIcon) (-4).dp else 0.dp),
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconTint
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        @OptIn(kotlin.time.ExperimentalTime::class)
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "tabs"
            ) {
                composable("tabs") {
                    when (selectedIndex) {
                        0 -> HomeScreen(navController)
                        1 -> TimerScreen()
                        2 -> HabitCategoryScreen(navController)
                        3-> HabitTrackerStatsScreen()
                        4 -> ProfileScreen()
                        else -> PlaceholderScreen()
                    }
                }

                composable("habit_form/{category}") { backStackEntry ->
                    val categoryTag = backStackEntry.arguments?.getString("category") ?: "Other"
                    HabitFormScreen(navController, categoryTag)
                }

                composable(
                    "habit_details/{habitId}/{selectedDate}",
                    arguments = listOf(
                        navArgument("habitId") { type = NavType.IntType },
                        navArgument("selectedDate") { type = NavType.StringType }
                    )
                ) {
                    val habitId = it.arguments?.getInt("habitId") ?: 0
                    val selectedDate = it.arguments?.getString("selectedDate") ?: LocalDate.now().toString()
                    HabitDetailsScreen(habitId = habitId, selectedDate = selectedDate, navController = navController)
                }







            }

        }

    }

}


@Composable
fun PlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Screen coming soon...", color = Color.Gray)
    }
}


@Preview(showSystemUi = true)
@Composable
fun NavScreenPreview() {
    NavScreen()
}