package com.example.habit_compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Show error message if present
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) {
            // Clear error after showing
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Statistics header
        Text(
            text = "Your Statistics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Streak Cards
        StreakCards(
            currentStreak = uiState.currentStreak,
            longestStreak = uiState.longestStreak,
            isLoading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Badges section
        BadgesSection(badges = uiState.badges)

        Spacer(modifier = Modifier.height(24.dp))

        // Vacation mode indicator
        if (uiState.isVacationMode) {
            VacationModeCard(
                startDate = uiState.vacationStartDate,
                onDisable = { viewModel.setVacationMode(false) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Chart period selector and charts
        ChartSection(
            selectedPeriod = uiState.selectedChartPeriod,
            onPeriodSelected = { viewModel.setChartPeriod(it) },
            chartData = uiState.chartData,
            isLoading = uiState.isLoadingChart
        )
    }
}

@Composable
fun StreakCards(
    currentStreak: Int,
    longestStreak: Int,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Current Streak Card
        StreakCard(
            title = "Current Streak",
            streak = currentStreak,
            icon = Icons.Default.Whatshot,
            color = MaterialTheme.colorScheme.primary,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Longest Streak Card
        StreakCard(
            title = "Best Streak",
            streak = longestStreak,
            icon = Icons.Default.EmojiEvents,
            color = MaterialTheme.colorScheme.tertiary,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StreakCard(
    title: String,
    streak: Int,
    icon: ImageVector,
    color: Color,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = color
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "$streak",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )

                        Text(
                            text = " days",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesSection(badges: List<Badge>) {
    Column {
        Text(
            text = "Your Badges",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (badges.isEmpty()) {
            Text(
                text = "You haven't earned any badges yet. Keep going!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                badges.filter { it.isEarned }.forEach { badge ->
                    BadgeItem(badge = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = with(Modifier) {
                size(56.dp)
                        .clip(CircleShape)
                        .background(badge.color.copy(alpha = 0.2f))
            },
            contentAlignment = Alignment.Center
        ) {
            // Use badge icon name to determine which icon to display
            when (badge.iconName) {
                "star" -> Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = badge.name,
                    tint = badge.color,
                    modifier = Modifier.size(32.dp)
                )
                "trending_up" -> Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = badge.name,
                    tint = badge.color,
                    modifier = Modifier.size(32.dp)
                )
                "local_fire_department" -> Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = badge.name,
                    tint = badge.color,
                    modifier = Modifier.size(32.dp)
                )
                "emoji_events" -> Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = badge.name,
                    tint = badge.color,
                    modifier = Modifier.size(32.dp)
                )
                "beach_access" -> Icon(
                    imageVector = Icons.Default.BeachAccess,
                    contentDescription = badge.name,
                    tint = badge.color,
                    modifier = Modifier.size(32.dp)
                )
                else -> Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = badge.name,
                    tint = badge.color,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Text(
            text = badge.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun VacationModeCard(
    startDate: String?,
    onDisable: () -> Unit
) {
    val formattedDate = startDate?.let {
        try {
            val date = LocalDate.parse(it)
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            date.format(formatter)
        } catch (e: Exception) {
            it
        }
    } ?: "Unknown"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BeachAccess,
                contentDescription = "Vacation Mode",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Vacation Mode Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Since $formattedDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onDisable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("End")
            }
        }
    }
}

@Composable
fun ChartSection(
    selectedPeriod: ChartPeriod,
    onPeriodSelected: (ChartPeriod) -> Unit,
    chartData: List<ChartData>,
    isLoading: Boolean
) {
    Column {
        Text(
            text = "Completion Rates",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Period selector tabs
        ChartPeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chart
        CompletionRateChart(
            chartData = chartData,
            isLoading = isLoading
        )
    }
}

@Composable
fun ChartPeriodSelector(
    selectedPeriod: ChartPeriod,
    onPeriodSelected: (ChartPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ChartPeriod.values().forEach { period ->
            val isSelected = period == selectedPeriod
            val backgroundColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent
            }
            val textColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { onPeriodSelected(period) },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = textColor
                    )
                ) {
                    Text(
                        text = when (period) {
                            ChartPeriod.DAILY -> "Daily"
                            ChartPeriod.WEEKLY -> "Weekly"
                            ChartPeriod.MONTHLY -> "Monthly"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
/**
 * Displays a chart showing completion rates based on provided data.
 * Handles loading states and empty data scenarios with appropriate feedback.
 *
 * @param chartData The data to display in the chart
 * @param isLoading Whether the data is currently loading
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun CompletionRateChart(
    chartData: List<ChartData>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(top = 8.dp)
            .semantics {
                contentDescription = "Completion rate chart showing your habit tracking history"
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            chartData.isEmpty() -> {
                EmptyChartState()
            }
            else -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    // Delegate to BarChart for the actual chart rendering
                    BarChart(
                        chartData = chartData,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Displays a visually appealing empty state for the chart
 */
@Composable
private fun EmptyChartState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.InsertChartOutlined,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = "No data available for this period",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * A bar chart visualization of completion data
 *
 * @param chartData List of data points to display in the chart
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun BarChart(
    chartData: List<ChartData>,
    modifier: Modifier = Modifier
) {
    // Calculate the maximum value once to avoid recalculation during composition
    val maxValue = remember(chartData) {
        chartData.maxOfOrNull { it.value }?.coerceAtLeast(0.1f) ?: 1f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        // Bar chart
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            chartData.forEach { data ->
                // Animate the height of each bar
                val animatedHeight by animateFloatAsState(
                    targetValue = if (data.value > 0f) (data.value / maxValue).coerceAtLeast(0.05f) else 0.05f,
                    // Use spring animation for more natural feel
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "barHeight"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Bar with accessibility support
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight(animatedHeight)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (data.isToday) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            .semantics {
                                contentDescription = "Bar representing ${data.value} completion rate for ${data.label}" +
                                        if (data.isToday) ", which is today" else ""
                            }
                    )
                }
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            chartData.forEach { data ->
                Text(
                    text = data.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (data.isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = if (data.isToday) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
