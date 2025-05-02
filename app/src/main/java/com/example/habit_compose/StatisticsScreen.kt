

package com.example.habit_compose

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Rocket
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.res.Configuration



@Composable
fun HabitTrackerStatsScreen() {
    val selectedTabIndex = remember { mutableStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TitleSection(colorScheme.onBackground)
            HighlightsSection(colorScheme.primary, colorScheme.surfaceVariant, colorScheme.onSurfaceVariant)
            BadgesSection(colorScheme.onBackground, colorScheme.primary, colorScheme.surfaceVariant, colorScheme.onSurfaceVariant)
            ChartSection(selectedTabIndex.value, colorScheme.primary, colorScheme.onBackground, colorScheme.onSurfaceVariant) {
                selectedTabIndex.value = it
            }
        }
    }
}

@Composable
private fun TitleSection(onBackgroundColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor
            )
        )
        Text(
            text = "Track your habit stats",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = onBackgroundColor.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
private fun HighlightsSection(
    primaryColor: Color,
    surfaceVariantColor: Color,
    onSurfaceVariantColor: Color
) {
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
                icon = Icons.Outlined.LocalFireDepartment,
                primaryColor = primaryColor,
                surfaceVariantColor = surfaceVariantColor,
                onSurfaceVariantColor = onSurfaceVariantColor
            )
            HighlightCard(
                modifier = Modifier.weight(1f),
                title = "7 Days",
                subtitle = "Longest streak",
                icon = Icons.Outlined.Rocket,
                primaryColor = primaryColor,
                surfaceVariantColor = surfaceVariantColor,
                onSurfaceVariantColor = onSurfaceVariantColor
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
                icon = Icons.Outlined.CalendarMonth,
                primaryColor = primaryColor,
                surfaceVariantColor = surfaceVariantColor,
                onSurfaceVariantColor = onSurfaceVariantColor
            )
            HighlightCard(
                modifier = Modifier.weight(1f),
                title = "Running",
                subtitle = "Focus on",
                icon = Icons.Outlined.DirectionsRun,
                primaryColor = primaryColor,
                surfaceVariantColor = surfaceVariantColor,
                onSurfaceVariantColor = onSurfaceVariantColor
            )
        }
    }
}

@Composable
private fun HighlightCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    primaryColor: Color,
    surfaceVariantColor: Color,
    onSurfaceVariantColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = surfaceVariantColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(primaryColor)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = onSurfaceVariantColor
                    )
                )
            }
        }
    }
}

@Composable
private fun BadgesSection(
    onBackgroundColor: Color,
    primaryColor: Color,
    surfaceVariantColor: Color,
    onSurfaceVariantColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Badges",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor
            )
        )

        val badges = listOf(
            Badge("7 Days", "Completed", true),
            Badge("14 Days", "Completed", false),
            Badge("21 Days", "Completed", false),
            Badge("28 Days", "Completed", false),
            Badge("35 Days", "Completed", false)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(badges) { badge ->
                BadgeItem(
                    badge = badge,
                    primaryColor = primaryColor,
                    surfaceVariantColor = surfaceVariantColor,
                    onSurfaceVariantColor = onSurfaceVariantColor,
                    onBackgroundColor = onBackgroundColor
                )
            }
        }
    }
}

@Composable
private fun BadgeItem(
    badge: Badge,
    primaryColor: Color,
    surfaceVariantColor: Color,
    onSurfaceVariantColor: Color,
    onBackgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(if (badge.isUnlocked) primaryColor else surfaceVariantColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (badge.isUnlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                contentDescription = if (badge.isUnlocked) "Unlocked" else "Locked",
                tint = if (badge.isUnlocked) Color.White else onSurfaceVariantColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = badge.days,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) onBackgroundColor else onBackgroundColor.copy(alpha = 0.5f)
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = badge.label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (badge.isUnlocked) onSurfaceVariantColor else onSurfaceVariantColor.copy(alpha = 0.5f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChartSection(
    selectedTabIndex: Int,
    primaryColor: Color,
    onBackgroundColor: Color,
    onSurfaceVariantColor: Color,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = primaryColor,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(3.dp)
                        .background(
                            color = primaryColor,
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )
            },
            divider = {}
        ) {
            Tab(selected = selectedTabIndex == 0, onClick = { onTabSelected(0) }) {
                Text("Daily", color = if (selectedTabIndex == 0) primaryColor else onBackgroundColor)
            }
            Tab(selected = selectedTabIndex == 1, onClick = { onTabSelected(1) }) {
                Text("Weekly", color = if (selectedTabIndex == 1) primaryColor else onBackgroundColor)
            }
            Tab(selected = selectedTabIndex == 2, onClick = { onTabSelected(2) }) {
                Text("Monthly", color = if (selectedTabIndex == 2) primaryColor else onBackgroundColor)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 8.dp)
        ) {
            BarChart(primaryColor, onSurfaceVariantColor)
        }
    }
}

@Composable
private fun BarChart(primaryColor: Color, onSurfaceVariantColor: Color) {
    val percentages = listOf(1.0f, 0.7f, 1.0f, 0.5f, 0.9f)
    val dayLabels = listOf("Sat", "Sun", "Mon", "Tue", "Today")
    val colorScheme = MaterialTheme.colorScheme // Get colorScheme here as well

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.width(40.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("100%", "80%", "60%", "40%", "20%", "0%").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f) // Use colorScheme
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barWidth = (size.width - (percentages.size - 1) * 16.dp.toPx()) / percentages.size
                    val stepSize = size.height / 5

                    // Draw guide lines
                    for (i in 0..5) {
                        val y = size.height - i * stepSize
                        drawLine(
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.3f), // Use colorScheme
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw bars
                    percentages.forEachIndexed { index, percentage ->
                        val barHeight = percentage * size.height
                        val left = index * (barWidth + 16.dp.toPx())
                        val top = size.height - barHeight
                        val isToday = index == percentages.lastIndex

                        drawRoundRect(
                            color = if (isToday) colorScheme.primary else colorScheme.primary.copy(alpha = 0.5f), // Use colorScheme
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEachIndexed { index, day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (index == dayLabels.size - 1) colorScheme.primary // Use colorScheme
                        else colorScheme.onSurfaceVariant.copy(alpha = 0.7f) // Use colorScheme
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun HabitTrackerStatsScreenPreview() {
    MaterialTheme {
        HabitTrackerStatsScreen()
    }
}

private data class Badge(val days: String, val label: String, val isUnlocked: Boolean)