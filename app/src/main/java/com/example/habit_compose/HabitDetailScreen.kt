package com.example.habit_compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HabitDetailsScreen(habitId: Int, selectedDate: String,navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var habit by remember { mutableStateOf<Habit?>(null) }
    val scope = rememberCoroutineScope()
    var completedCount by remember { mutableStateOf(0) }

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            val foundHabit = db.habitDao().getAllHabits().find { it.id == habitId }
            withContext(Dispatchers.Main) {
                habit = foundHabit
                completedCount = foundHabit?.completedCount ?: 0
            }
        }
    }

    habit?.let {
        val category = habitCategories.find { cat -> cat.title == it.categoryTag }
        val totalTimes = it.howOftenPerDay
        val progress = (completedCount.toFloat() / totalTimes).coerceIn(0f, 1f)


        Box(
                modifier = Modifier
                    .fillMaxSize()

                    .background(Color(0xFFF5F5FF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = it.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3C3C3C)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = category?.illustration ?: R.drawable.reading),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(180.dp)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(10.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(50.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFD8F0FF))
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFF6D6D6D)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            "Date and Time",
                                            color = Color.DarkGray,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = LocalDate.parse(selectedDate).format(
                                                DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )

                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Goal Previews",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF333333)
                                )

                                Box(
                                    modifier = Modifier.size(140.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val canvasWidth = size.width
                                        val canvasHeight = size.height

                                        drawArc(
                                            color = Color(0xFF2196F3),
                                            startAngle = -90f,
                                            sweepAngle = progress * 360f,
                                            useCenter = false,
                                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                                        )

                                        val padding = 18f
                                        drawArc(
                                            color = Color(0xFFFF7EB3),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = 14f, cap = StrokeCap.Round),
                                            topLeft = Offset(padding, padding),
                                            size = Size(
                                                canvasWidth - 2 * padding,
                                                canvasHeight - 2 * padding
                                            )
                                        )
                                    }

                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "progress task: ${(progress * 100).toInt()}%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF5A5A5A)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (completedCount < totalTimes) {
                                            completedCount++
                                            scope.launch(Dispatchers.IO) {
                                                db.habitDao().updateCompletedCount(habitId, completedCount)
                                            }
                                        }
                                    },
                                    enabled = completedCount < totalTimes,
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFCCF2FF)
                                    ),
                                    modifier = Modifier
                                        .height(46.dp)
                                        .width(190.dp)
                                ) {
                                    Text(
                                    text = if (progress >= 1f) "Done" else "Complete Task",
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

