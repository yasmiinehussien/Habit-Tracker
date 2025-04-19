package com.example.habit_compose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habit_compose.ui.theme.Habit_composeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Habit_composeTheme {
                val navController = rememberNavController()

                // ✅ Navigation graph directly here
                NavHost(
                    navController = navController,
                    startDestination = "habit_list"
                ) {
                    composable("habit_list") {
                        Scaffold(modifier = Modifier.fillMaxSize()) {
                            HabitCategoryScreen(navController)
                        }
                    }
                    composable("habit_form") {
                        Scaffold(modifier = Modifier.fillMaxSize()) {
                            HabitFormScreen()
                        }
                    }
                }
            }
        }
    }
}