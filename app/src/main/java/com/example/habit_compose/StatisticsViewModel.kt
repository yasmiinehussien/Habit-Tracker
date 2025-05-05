package com.example.habit_compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // Track whether initial data has been loaded
    private var initialDataLoaded = false

    init {
        loadStreakData()
        observeVacationMode()
        loadChartData()
    }

    private fun loadStreakData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val currentStreak = statisticsRepository.getCurrentStreak()
                val longestStreak = statisticsRepository.getLongestStreak()

                _uiState.value = _uiState.value.copy(
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading streak data: ${e.message}"
                )
            }
        }
    }

    private fun observeVacationMode() {
        viewModelScope.launch {
            // Combine vacation mode and badges to update UI state
            combine(
                statisticsRepository.getVacationMode(),
                statisticsRepository.getBadges()
            ) { vacationMode, badges ->
                _uiState.value = _uiState.value.copy(
                    isVacationMode = vacationMode?.isEnabled ?: false,
                    vacationStartDate = vacationMode?.startDate,
                    badges = badges
                )
            }.collect()
        }
    }

    private fun loadChartData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingChart = true)

            try {
                val chartData = when (_uiState.value.selectedChartPeriod) {
                    ChartPeriod.DAILY -> statisticsRepository.getDailyChartData()
                    ChartPeriod.WEEKLY -> statisticsRepository.getWeeklyChartData()
                    ChartPeriod.MONTHLY -> statisticsRepository.getMonthlyChartData()
                }

                _uiState.value = _uiState.value.copy(
                    chartData = chartData,
                    isLoadingChart = false
                )

                initialDataLoaded = true
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingChart = false,
                    error = "Error loading chart data: ${e.message}"
                )
            }
        }
    }

    fun setChartPeriod(period: ChartPeriod) {
        if (_uiState.value.selectedChartPeriod != period) {
            _uiState.value = _uiState.value.copy(
                selectedChartPeriod = period,
                isLoadingChart = true
            )

            viewModelScope.launch {
                try {
                    val chartData = when (period) {
                        ChartPeriod.DAILY -> statisticsRepository.getDailyChartData()
                        ChartPeriod.WEEKLY -> statisticsRepository.getWeeklyChartData()
                        ChartPeriod.MONTHLY -> statisticsRepository.getMonthlyChartData()
                    }

                    _uiState.value = _uiState.value.copy(
                        chartData = chartData,
                        isLoadingChart = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoadingChart = false,
                        error = "Error loading chart data: ${e.message}"
                    )
                }
            }
        }
    }

    fun setVacationMode(isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                statisticsRepository.setVacationMode(isEnabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error setting vacation mode: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshData() {
        loadStreakData()
        loadChartData()
    }
}