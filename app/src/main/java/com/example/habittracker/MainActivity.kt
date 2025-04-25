package com.example.habittracker

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                NavScreen()
                // No need to pass context to LoginScreen, instead use LocalContext.current inside LoginScreen
                LoginScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val calenderData = CalenderData()
    val today = calenderData.today
    val weekDates = calenderData.getWeekDates()

    val selectedDate = rememberSaveable { mutableStateOf(today) }
    val displayedDate = selectedDate.value ?: today

    Column() {
        HeadIcons()

        LazyRow(
            modifier.background(Color(0xFFE5E8E5))
        ) {
            items(items = weekDates) { date ->
                DateBar(
                    day = date.day,
                    date = date.date.dayOfMonth.toString(),
                    isSelected = date.date == displayedDate,
                    onClick = { selectedDate.value = date.date }
                )
            }
        }
        val habitList = listOf(
            "habit 1",
            "habit 2",
            "habit 3",
            "habit 4",
            "habit 5",
            "habit 6",
            "habit 7",
            "habit 8"
        )
        Column(
            modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {

            Text(text = "Habits", fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(top = 22.dp)

            ) {
                items(habitList) { item ->
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp)

                    )
                }
            }
        }
    }
}

@Composable
fun HeadIcons() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 28.dp)
            .padding(top = 10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = {/*to calender */ }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "date",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray
                )
            }
            IconButton(onClick = {/*to notification screen */ }) {
                Icon(
                    imageVector = Icons.TwoTone.Notifications,
                    contentDescription = "notification icon",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray,
                )
            }
        }
        Text(
            // default for ui ,should take username from firebase
            text = "Hi,username ",
            fontFamily = FontFamily(Typeface.DEFAULT_BOLD),
            fontSize = 21.sp,
        )
        Text(
            text = "Let's make habits together!",
            color = Color.Gray,
            modifier = Modifier
                .padding(top = 10.dp)
        )

    }
}

@Composable
fun DateBar(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 3.dp, horizontal = 12.dp),
        border = if (isSelected) BorderStroke(
            2.dp,
            Color(0xFF449144)
        ) else null,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    )
    {
        Column(
            modifier = Modifier
                .width(44.dp)
                .height(48.dp)
                .padding(4.dp)
        ) {
            Text(
                text = date,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color(0xFF449144)
                else Color.Black
            )
            Text(
                text = day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color(0xFF449144)
                else Color.Black
            )

        }
    }
}

@Composable
fun LoginScreen() {
    // LocalContext will automatically provide the context
    val context = LocalContext.current
    // Implement login logic here, using context as needed
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeadIconsPreview() {
    HomeScreen()
}
