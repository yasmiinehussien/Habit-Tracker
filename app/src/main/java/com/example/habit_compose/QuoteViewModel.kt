package com.example.habit_compose
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Quote screen
 */
data class QuoteUiState(
    val quote: Quote? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for handling quote data and UI state
 */
class QuoteViewModel(private val repository: QuoteRepository = QuoteRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    /**
     * Fetches a random quote from the repository
     */
    fun fetchRandomQuote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getRandomQuote()
                .onSuccess { quote ->
                    _uiState.update {
                        it.copy(
                            quote = quote,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message ?: "Unknown error occurred",
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Toggles the liked state of the current quote
     */
    fun toggleLike() {
        _uiState.value.quote?.let { currentQuote ->
            _uiState.update {
                it.copy(
                    quote = currentQuote.copy(isLiked = !currentQuote.isLiked)
                )
            }
        }
    }
}