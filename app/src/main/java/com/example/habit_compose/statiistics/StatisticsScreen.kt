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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Define colors
val PrimaryColor = Color(0xFF9472EA)
val PrimaryLightColor = Color(0xFFB39DFF)
val BackgroundColor = Color(0xFFF5F5F5)
val TextPrimaryColor = Color(0xFF333333)
val TextSecondaryColor = Color(0xFF666666)

@Composable
fun HabitTrackerStatsScreen(onBack:()->Unit={}) {
    BackHandler { onBack() }
    val selectedTabIndex = remember { mutableStateOf(0) }

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
            // Title Section
            TitleSection()

            // Highlights Section
            HighlightsSection()

            // Badges Section
            BadgesSection()

            // Chart Section
            ChartSection(selectedTabIndex.value) { selectedTabIndex.value = it }
        }
    }
}

@Composable
fun TitleSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )
        Text(
            text = "Track your habit stats",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondaryColor
            )
        )
    }
}

@Composable
fun HighlightsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HighlightCard(
                modifier = Modifier.weight(1f),
                title = "3 Days",
                subtitle = "Current streak",
                icon = Icons.Outlined.LocalFireDepartment
            )

            HighlightCard(
                modifier = Modifier.weight(1f),
                title = "7 Days",
                subtitle = "Longest streak",
                icon = Icons.Outlined.Rocket
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HighlightCard(
                modifier = Modifier.weight(1f),
                title = "April",
                subtitle = "Best Month",
                icon = Icons.Outlined.CalendarMonth
            )

            HighlightCard(
                modifier = Modifier.weight(1f),
                title = "Running",
                subtitle = "Focus on",
                icon = Icons.Outlined.DirectionsRun
            )
        }
    }
}

@Composable
fun HighlightCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = PrimaryColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryColor
                    )
                )
            }
        }
    }
}

@Composable
fun BadgesSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Badges",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )

        val badges = listOf(
            Badge("7 Days", "Completed", true),
            Badge("14 Days", "Completed", false),
            Badge("21 Days", "Completed", false),
            Badge("28 Days", "Completed", false),
            Badge("35 Days", "Completed", false)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(badges) { badge ->
                BadgeItem(badge)
            }
        }
    }
}

data class Badge(val days: String, val label: String, val isUnlocked: Boolean)

@Composable
fun BadgeItem(badge: Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    if (badge.isUnlocked) PrimaryColor
                    else PrimaryColor.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (badge.isUnlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                contentDescription = if (badge.isUnlocked) "Unlocked Badge" else "Locked Badge",
                tint = if (badge.isUnlocked) Color.White else PrimaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = badge.days,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) TextPrimaryColor else TextSecondaryColor.copy(alpha = 0.5f)
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = badge.label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (badge.isUnlocked) TextSecondaryColor else TextSecondaryColor.copy(alpha = 0.5f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChartSection(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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

        // Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 8.dp)
        ) {
            BarChart()
        }
    }
}

@Composable
fun BarChart() {
    // Sample data - percentage values for each day
    val percentages = listOf(1.0f, 0.7f, 1.0f, 0.5f, 0.9f)
    val dayLabels = listOf("Sat", "Sun", "Mon", "Tue", "Today")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Y-axis labels
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

            // Chart
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barWidth = (size.width - (percentages.size - 1) * 16.dp.toPx()) / percentages.size

                    // Draw horizontal guide lines
                    val stepSize = size.height / 5
                    for (i in 0..5) {
                        val y = size.height - i * stepSize
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw bars
                    percentages.forEachIndexed { index, percentage ->
                        val isToday = index == percentages.lastIndex
                        val barColor = if (isToday) PrimaryColor else PrimaryLightColor

                        val barHeight = percentage * size.height
                        val left = index * (barWidth + 16.dp.toPx())
                        val top = size.height - barHeight

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEachIndexed { index, day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (index == dayLabels.lastIndex) PrimaryColor else TextSecondaryColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(40.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitTrackerStatsScreenPreview() {
    MaterialTheme {
        HabitTrackerStatsScreen()
    }
}