package com.example.habit_compose.home

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.habit_compose.habits.AppDatabase
import com.example.habit_compose.habits.Habit
import com.example.habit_compose.habits.HabitCategory
import com.example.habit_compose.R
import com.example.habit_compose.habits.habitCategories

import com.example.habit_compose.ui.theme.AppTheme
import com.example.habit_compose.data.UserPreferencesDataStore
import com.example.habit_compose.profile.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // جلب حالة الوضع الداكن قبل تحميل واجهة المستخدم
        val userPreferences = UserPreferencesDataStore(this)
        val initialDarkMode = runBlocking { userPreferences.isDarkModeEnabled.first() }

        setContent {
            // إعداد ViewModel للبروفايل
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(this)
            )

            // مراقبة حالة الوضع الداكن
            val darkModeEnabled by profileViewModel.isDarkModeEnabled.collectAsState()

            // تطبيق السمة المناسبة
            AppTheme(darkTheme = darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavScreen()
                }
            }
        }
    }
}

fun mapDayOfWeekToIndex(day: java.time.DayOfWeek): Int {
    return when (day) {
        java.time.DayOfWeek.SUNDAY -> 0
        java.time.DayOfWeek.MONDAY -> 1
        java.time.DayOfWeek.TUESDAY -> 2
        java.time.DayOfWeek.WEDNESDAY -> 3
        java.time.DayOfWeek.THURSDAY -> 4
        java.time.DayOfWeek.FRIDAY -> 5
        java.time.DayOfWeek.SATURDAY -> 6
    }
}

@Composable
fun HabitListFromDb(habits: List<Habit>, navController: NavController, selectedDate: LocalDate) {
    val categoryMap = habitCategories.associateBy { it.title }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if(MaterialTheme.colorScheme.isLight()) Color.White else Color.DarkGray)
            .fillMaxSize()
    ) {
        if (habits.isEmpty()) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("habit_animation.json"))
            val progress by animateLottieCompositionAsState(composition)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie Animation
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(250.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Let's add your first habit!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF7D54D2)
                )
            }
        }
        else {
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
                        HabitCardFromDb(habit, category, navController, selectedDate)
                    }
                }
            }
        }
    }
}

fun getUsername(): String {
    val user = FirebaseAuth.getInstance().currentUser
    val emailUsername = user?.email?.substringBefore("@")?.let {
        // Remove any numbers at the beginning of the email address
        it.replace(Regex("^[0-9]+"), "")
    }
    return user?.displayName ?: emailUsername ?: "Guest"
}



@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }


    val auth = FirebaseAuth.getInstance()
    var savedHabits by remember { mutableStateOf(emptyList<Habit>()) }
    val deletedHabitId by navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Int?>("deleted_habit_id", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(deletedHabitId) {
        deletedHabitId?.let { id ->
            savedHabits = savedHabits.filter { it.id != id }
            navController.currentBackStackEntry?.savedStateHandle?.set("deleted_habit_id", null)
        }
    }

    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = Habits, 1 = Tasks
    val scope = rememberCoroutineScope()

    val calenderData = remember { CalenderData() }
    val today = calenderData.today
    val weekDates = calenderData.getWeekDates()
    val selectedDate = rememberSaveable { mutableStateOf(today) }

    fun loadHabits(dateSelected: LocalDate) {
        scope.launch(Dispatchers.IO) {
            val habits = db.habitDao().getAllRegularHabits()
            val selectedDayOfWeek = mapDayOfWeekToIndex(dateSelected.dayOfWeek).toString()

            val filteredHabits = habits.filter { habit ->
                val endsAfterOrEqual = habit.endDate.isNullOrEmpty() ||
                        LocalDate.parse(habit.endDate).isAfter(dateSelected) ||
                        LocalDate.parse(habit.endDate).isEqual(dateSelected)

                val startsBeforeOrEqual = LocalDate.parse(habit.createdDate).isBefore(dateSelected) ||
                        LocalDate.parse(habit.createdDate).isEqual(dateSelected)

                if (habit.repeatFrequency == "Daily") {
                    endsAfterOrEqual && startsBeforeOrEqual
                } else if (habit.repeatFrequency == "Weekly") {
                    habit.daysSelected.split(",").contains(selectedDayOfWeek) &&
                            endsAfterOrEqual && startsBeforeOrEqual
                } else {
                    false
                }
            }

            withContext(Dispatchers.Main) {
                savedHabits = filteredHabits
            }
        }
    }

    fun loadTasks(dateSelected: LocalDate) {
        scope.launch(Dispatchers.IO) {
            val tasks = db.habitDao().getAllOneTimeTasks()

            val filteredTasks = tasks.filter { task ->
                task.taskDate == dateSelected.toString()
            }

            withContext(Dispatchers.Main) {
                savedHabits = filteredTasks
            }
        }
    }

    LaunchedEffect(selectedTab, selectedDate.value, savedHabits) {
        if (selectedTab == 0) {
            loadHabits(selectedDate.value)
        } else {
            loadTasks(selectedDate.value) // pass selected date here
        }
    }

    val GreenPrimary = Color(0xFF7A49D5)
    val LightGreenSurface = if(MaterialTheme.colorScheme.isLight()) Color(0xFFE0F2E9) else Color(0xFF2A3439)

    Column {
        // Pass the navController to HeadIcons here
        HeadIcons(navController = navController)

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

                        scope.launch(Dispatchers.IO) {
                            val allHabits = db.habitDao().getAllHabits()

                            val selectedDayOfWeek = mapDayOfWeekToIndex(date.date.dayOfWeek).toString()
                            val selectedDateStr = date.date.toString()

                            val filtered = allHabits.filter { habit ->
                                if (selectedTab == 0 && habit.isRegularHabit) {
                                    // Habits tab: check repeat type
                                    //val selectedDateStr = date.date.toString()

                                    val endsAfterOrEqual = habit.endDate.isNullOrEmpty() ||
                                            LocalDate.parse(habit.endDate).isAfter(date.date) ||
                                            LocalDate.parse(habit.endDate).isEqual(date.date)

                                    val startsBeforeOrEqual =
                                        LocalDate.parse(habit.createdDate).isBefore(date.date) ||
                                                LocalDate.parse(habit.createdDate).isEqual(date.date)

                                    if (habit.repeatFrequency == "Daily") {
                                        endsAfterOrEqual && startsBeforeOrEqual
                                    } else if (habit.repeatFrequency == "Weekly") {
                                        habit.daysSelected.split(",").contains(selectedDayOfWeek) &&
                                                endsAfterOrEqual && startsBeforeOrEqual
                                    } else {
                                        false // avoid accidentally including incorrect frequency
                                    }
                                } else if (selectedTab == 1 && !habit.isRegularHabit) {
                                    habit.taskDate == selectedDateStr
                                    // Tasks tab: show tasks only for exact taskDate
                                } else {
                                    false
                                }
                            }

                            withContext(Dispatchers.Main) {
                                savedHabits = filtered
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
                            color = if (selectedTab == index) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Habit/Task Cards
        HabitListFromDb(
            habits = savedHabits,
            navController = navController,
            selectedDate = selectedDate.value
        )
    }
}

// Extension function to check if color scheme is light
@Composable
fun androidx.compose.material3.ColorScheme.isLight(): Boolean {
    return this.background.luminance() > 0.5f
}

@Composable
fun HeadIcons(navController: NavController) {
    val username = getUsername()

    // تحسين تنسيق الهيدر: نقل كل العناصر إلى صف واحد
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        // الصف الرئيسي - اسم المستخدم على اليمين وأيقونة الاقتباسات على اليسار
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // قسم اسم المستخدم على اليمين
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = username,
                    fontFamily = FontFamily(Typeface.DEFAULT_BOLD),
                    fontSize = 21.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Let's make habits together!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // أيقونة الاقتباسات على اليسار
            IconButton(
                onClick = { navController.navigate("quotes") },
                modifier = Modifier
                    .size(48.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Notifications,
                    contentDescription = "Motivational Quotes",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun DateBar(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 3.dp, horizontal = 14.dp)
            .clip(RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF6200EA) else if(MaterialTheme.colorScheme.isLight()) Color(0xFFF6FEFF) else Color(0xFF2A3439)
        ),
    ) {
        Column(
            modifier = Modifier
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
                    .background(if(MaterialTheme.colorScheme.isLight()) Color(0xFFFDFDFD) else Color(0xFF2E2E2E))
            ) {
                Text(
                    text = date,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface
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
                    color = if (isSelected) Color.White else Color(0xFF55C9D7)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    NavScreen()
}

@Composable
fun HabitCardFromDb(habit: Habit, category: HabitCategory?, navController: NavController, selectedDate: LocalDate) {
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(30f) }

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

    val cardBackground = if(MaterialTheme.colorScheme.isLight()) Color.White else Color(0xFF2E2E2E)

    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
            .clickable {
                navController.navigate("habit_details/${habit.id}/${selectedDate}")
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
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = if(MaterialTheme.colorScheme.isLight())
                                listOf(Color(0xFFDFF5EC), Color.Transparent)
                            else
                                listOf(Color(0xFF2A3439), Color.Transparent)
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = category?.tagColor?.copy(alpha = 0.15f) ?: Color.LightGray,
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
                            color = category?.tagColor ?: Color.Black,
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