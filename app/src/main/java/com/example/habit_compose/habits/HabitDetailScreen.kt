package com.example.habit_compose.habits


import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.habit_compose.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HabitDetailsScreen(habitId: Int, selectedDate: String, navController: NavController) {
    val today = LocalDate.now()
    val selected = LocalDate.parse(selectedDate)
    val context = LocalContext.current
    val isToday = selected == today
    val isPast = selected.isBefore(today)
    val isFuture = selected.isAfter(today)

    var showDialog by remember { mutableStateOf(false) }


    val db = remember { AppDatabase.getDatabase(context) }
    var habit by remember { mutableStateOf<Habit?>(null) }
    val scope = rememberCoroutineScope()
    var completedCount by remember { mutableStateOf(0) }
    var showDoneAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(habitId, selectedDate) {

        scope.launch(Dispatchers.IO) {
            val foundHabit = db.habitDao().getAllHabits().find { it.id == habitId }
            val habitProgress =
                db.habitProgressDao().getProgressForDate(habitId, selectedDate) // تعديل هنا

            withContext(Dispatchers.Main) {
                habit = foundHabit
                completedCount = habitProgress?.completedCount ?: foundHabit?.completedCount ?: 0
            }
        }
    }


    habit?.let {
        val category = habitCategories.find { cat -> cat.title == it.categoryTag }
        val totalTimes = it.howOftenPerDay
        val progress = if (isFuture) {
            0f
        } else {
            (completedCount.toFloat() / totalTimes).coerceIn(0f, 1f)
        }





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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
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
                                            "Date",
                                            color = Color.DarkGray,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = LocalDate.parse(selectedDate).format(
                                                DateTimeFormatter.ofPattern("dd MMM yyyy")
                                            ),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                IconButton(onClick = { showDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete habit",
                                        tint = Color.Gray
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
                                        color = Color(0xFF889389)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            if (showDoneAnimation) {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.Asset(
                                        "done_animation.json"
                                    )
                                )
                                val progressAnim by animateLottieCompositionAsState(
                                    composition,
                                    iterations = 1
                                )
                                LottieAnimation(
                                    composition = composition,
                                    progress = { progressAnim },
                                    modifier = Modifier
                                        .size(180.dp)
                                        .padding(bottom = 12.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    if (!isToday) {
                                        Toast.makeText(
                                            context,
                                            "You don't have access to modify this date's progress",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else if (completedCount < totalTimes) {
                                        scope.launch(Dispatchers.IO) {
                                            val existingProgress = db.habitProgressDao()
                                                .getProgressForDate(habitId, selectedDate)
                                            val newCount =
                                                (existingProgress?.completedCount ?: 0) + 1

                                            val progress = HabitProgress(
                                                habitId = habitId,
                                                date = selectedDate,
                                                completedCount = newCount
                                            )
                                            db.habitProgressDao().insertOrUpdateProgress(progress)

                                            withContext(Dispatchers.Main) {
                                                completedCount = newCount
                                                if (newCount >= totalTimes) {
                                                    showDoneAnimation = true
                                                    kotlinx.coroutines.delay(2000)
                                                    showDoneAnimation = false
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        !isToday -> Color.LightGray
                                        progress >= 1f -> Color(0xFFACE7AE)
                                        else -> Color(0xFFCCF2FF)
                                    }
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
//
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text("Delete Habit")
                        },
                        text = {
                            Text("Are you sure you want to delete this habit?")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                scope.launch(Dispatchers.IO) {
                                    db.habitDao().deleteHabitCompletely(habitId)
                                }
                                Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }) {
                                Text("Delete", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
