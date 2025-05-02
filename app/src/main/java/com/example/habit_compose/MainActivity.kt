package com.example.habit_compose

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.habit_compose.ui.theme.HabitTrackerTheme
import com.example.habit_compose.ui.theme.ThemeViewModel
import com.example.habit_compose.ui.theme.updateLocale

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale


















class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            HabitTrackerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                updateLocale(this, themeViewModel.selectedLanguage.value)

                // You can choose one of these:
                // ProfileScreen(themeViewModel)
                NavScreen()
            }
        }
    }
}


@Composable
fun HabitListFromDb(habits: List<Habit>, navController: NavController) {
    val categoryMap = habitCategories.associateBy { it.title }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if (habits.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("No habits yet", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    items(habits) { habit ->
                        val category = categoryMap[habit.categoryTag]
                        // or habit.categoryTag
                        HabitCardFromDb(habit, category, navController)
                    }
                }
            }
        }
    }
}

fun getNameFromEmail(): String {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: ""
    return email.substringBefore("@")
}


@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val colorScheme = MaterialTheme.colorScheme

    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = Habits, 1 = Tasks
    var savedHabits by remember { mutableStateOf(listOf<Habit>()) }

    val scope = rememberCoroutineScope()

    val calenderData = remember { CalenderData() }
    val today = calenderData.today
    val weekDates = calenderData.getWeekDates()
    val selectedDate = rememberSaveable { mutableStateOf(today) }

    fun loadHabits() {
        scope.launch(Dispatchers.IO) {
            val habits = db.habitDao().getAllRegularHabits()
            withContext(Dispatchers.Main) {
                savedHabits = habits
            }
        }
    }

    fun loadTasks() {
        scope.launch(Dispatchers.IO) {
            val tasks = db.habitDao().getAllOneTimeTasks()
            withContext(Dispatchers.Main) {
                savedHabits = tasks
            }
        }
    }

    // Load based on tab
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            loadHabits()
        } else {
            loadTasks()
        }
    }

    val GreenPrimary = colorScheme.primary // Using theme's primary color
    val LightGreenSurface = colorScheme.surface.copy(alpha = 0.3f) // Using theme's surface with slight alpha

    Column {
        HeadIcons()

        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(weekDates) { date ->
                DateBar(
                    day = date.day,
                    date = date.date.dayOfMonth.toString(),
                    isSelected = date.date == selectedDate.value,
                    onClick = {
                        selectedDate.value = date.date

                        if (selectedTab == 0) {
                            // Only reload habits when in habits tab
                            scope.launch(Dispatchers.IO) {
                                val dayOfWeekIndex = (date.date.dayOfWeek.value % 7).toString()
                                val habitsForDay = db.habitDao().getHabitsBySelectedDay(dayOfWeekIndex)
                                withContext(Dispatchers.Main) {
                                    savedHabits = habitsForDay
                                }
                            }
                        }
                    }
                )
            }
        }

        // 🟪 Tabs below the Calendar now
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightGreenSurface)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Habits", "Tasks").forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedTab == index) GreenPrimary else Color.Transparent)
                            .clickable {
                                selectedTab = index
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTab == index) colorScheme.onPrimary else colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Habit/Task Cards
        HabitListFromDb(habits = savedHabits, navController = navController)
    }
}


@Composable
fun HeadIcons() {
    val name = getNameFromEmail()
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 28.dp)
            .padding(top = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {/*to notification screen */ }) {
                Icon(
                    imageVector = Icons.TwoTone.Notifications,
                    contentDescription = "notification icon",
                    modifier = Modifier.size(28.dp),
                    tint = colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Adding some space
        Text(
            text = "Hello, $name",
            fontFamily = FontFamily(Typeface.DEFAULT_BOLD),
            fontSize = 21.sp,
            color = colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Let's make habits together!",
            color = colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .padding(top = 10.dp)
        )
    }
}

@Composable
fun DateBar(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 3.dp, horizontal = 14.dp)
            .clip(RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorScheme.primary else colorScheme.surface
        ),
    ) {
        Column(
            modifier = Modifier
                .width(50.dp)
                .height(68.dp)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(27.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp), clip = true
                    )
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .background(colorScheme.surface)
            ) {
                Text(
                    text = date,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp)
            ) {
                Text(
                    text = day,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.secondary
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    HabitTrackerTheme {
        NavScreen()
    }
}


@Composable
fun HabitCardFromDb(habit: Habit, category: HabitCategory?, navController: NavController) {
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(30f) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
        animatedOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        )
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
            .clickable {
                navController.navigate("habit_details/${habit.id}")
            }
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .fillMaxWidth()
            .then(Modifier.widthIn(max = 450.dp)) // ✅ Wider card with limit
            .height(260.dp)
            .padding(horizontal = 8.dp)
            .shadow(12.dp, RoundedCornerShape(26.dp)),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surface.copy(alpha = 0.7f), // Using surface with alpha
                                Color.Transparent
                            )
                        )
                    )
            )

            Image(
                painter = painterResource(id = category?.bgImage ?: R.drawable.back_yoga),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .alpha(0.07f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = category?.illustration ?: R.drawable.reading),
                        contentDescription = habit.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = habit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = category?.tagColor?.copy(alpha = 0.15f) ?: colorScheme.surfaceVariant,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .height(34.dp) // ✅ Less height
                        .widthIn(min = 140.dp) // ✅ Wider button
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = category?.tag ?: "Habit",
                            color = category?.tagColor ?: colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}