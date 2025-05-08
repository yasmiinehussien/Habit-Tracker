package com.example.habit_compose.quotes


/**
 * Data model for the response from ZenQuotes API
 */
data class QuoteResponse(
    val q: String, // quote text
    val a: String  // author
)

/**
 * UI model for quotes to be displayed in the app
 */
data class Quote(
    val text: String,
    val author: String,
    val isLiked: Boolean = false,
    val id: String
)
