package com.example.habit_compose

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val isLoadingChart: Boolean = true,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val badges: List<Badge> = emptyList(),
    val isVacationMode: Boolean = false,
    val vacationStartDate: String? = null,
    val chartData: List<ChartData> = emptyList(),
    val selectedChartPeriod: ChartPeriod = ChartPeriod.DAILY,
    val error: String? = null
)