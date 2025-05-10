package com.example.habit_compose.statiistics
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Rocket
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.habit_compose.habits.HabitProgressDao
import com.example.habit_compose.habits.MonthlyAvgProgress
import com.example.habit_compose.habits.WeeklyAvgProgress
import com.exyte.animatednavbar.utils.toDp
import com.exyte.animatednavbar.utils.toPxf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Define colors
val PrimaryColor = Color(0xFF9472EA)
val PrimaryLightColor = Color(0xFFB39DFF)
val BackgroundColor = Color(0xFFF5F5F5)
val TextPrimaryColor = Color(0xFF333333)
val TextSecondaryColor = Color(0xFF666666)

@Composable
fun HabitTrackerStatsScreen(
    onBack: () -> Unit = {},
    dao: HabitProgressDao // خليه يتبعت من فوق
) {
    BackHandler { onBack() }

    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(dao)
    )

    val selectedTabIndex = remember { mutableStateOf(0) }
    val dailyAverages by viewModel.dailyAverages.collectAsState()
    val weeklyAverages by viewModel.weeklyAverages.collectAsState()
    val monthlyAverages by viewModel.monthlyAverages.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.loadDailyAverages()
        viewModel.loadWeeklyAverages()
        viewModel.loadMonthlyAverages()
    }

    // val selectedTabIndex = remember { mutableStateOf(0) }
    //val dailyAverages by viewModel.dailyAverages.collectAsState()

    Scaffold(
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StatAnimation()
            Spacer(modifier = Modifier.weight(1f))
            // Chart Section
            ChartSection(
                selectedTabIndex = selectedTabIndex.value,
                onTabSelected = { selectedTabIndex.value = it },
                dailyAverages = dailyAverages,
                weeklyAverages = weeklyAverages,
                monthlyAverages = monthlyAverages
            )
        }
    }
}

@Composable
fun StatAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("stat-animation.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}


@Composable
fun ChartSection(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    dailyAverages: List<DailyAvgProgress>,
    weeklyAverages: List<WeeklyAvgProgress>,
    monthlyAverages: List<MonthlyAvgProgress>,
    modifier: Modifier = Modifier
) {


    val dayLabels = when (selectedTabIndex) {
        0 -> if (dailyAverages.isEmpty()) List(7) { "" } else {
            dailyAverages.map {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date = LocalDate.parse(it.date, formatter)
                date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
            }
        }
        1 -> if (weeklyAverages.isEmpty()) List(4) { "" } else {
            weeklyAverages.map { "Week ${it.weekNumber}" }
        }
        2 -> if (monthlyAverages.isEmpty()) List(12) { "" } else {
            monthlyAverages.map { it.month.toString() }
        }
        else -> List(7) { "" }
    }

    val percentages = when (selectedTabIndex) {
        0 -> if (dailyAverages.isEmpty()) List(7) { 0f } else {
            dailyAverages.map {
                (it.avgProgress.toFloat() / 100f).coerceIn(0f, 1f)
            }
        }
        1 -> if (weeklyAverages.isEmpty()) List(4) { 0f } else {
            weeklyAverages.map { (it.avgProgress.toFloat() / 100f).coerceIn(0f, 1f) }
        }
        2 -> if (monthlyAverages.isEmpty()) List(12) { 0f } else {
            monthlyAverages.map { (it.avgProgress.toFloat() / 100f).coerceIn(0f, 1f) }
        }
        else -> List(7) { 0f }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryColor,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(3.dp)
                        .background(
                            color = PrimaryColor,
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("Daily") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("Weekly") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) },
                text = { Text("Monthly") }
            )
        }

        // Bar Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 8.dp)

        ) {
            BarChart(percentages = percentages, dayLabels = dayLabels)
        }
    }
}



@Composable
fun BarChart(
    percentages: List<Float>,
    dayLabels: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الرسم البياني
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (label in listOf("100%", "80%", "60%", "40%", "20%", "0%")) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Bars (with left padding to align better under Y-axis)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 8.dp) // ✅ تحريك الأعمدة شِمال
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barSpacing = 16.dp.toPx()
                    val barWidth = (size.width - (percentages.size - 1) * barSpacing) / percentages.size
                    val stepSize = size.height / 5

                    // خطوط أفقية خلف الأعمدة
                    for (i in 0..5) {
                        val y = size.height - i * stepSize
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    percentages.forEachIndexed { index, percentage ->
                        val isToday = index == percentages.lastIndex
                        val barColor = if (isToday) PrimaryColor else PrimaryLightColor

                        val barHeight = percentage * size.height
                        val left = index * (barWidth + barSpacing)

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(left, size.height - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }
            }
        }


        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(dayLabels.size) { index ->
                Text(
                    text = dayLabels[index],
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (index == dayLabels.lastIndex) PrimaryColor else TextSecondaryColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(40.dp)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}