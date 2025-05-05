package com.example.habit_compose

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that handles quote data operations
 */
class QuoteRepository(private val api: ZenQuotesApi = ZenQuotesApi.create()) {

    /**
     * Fetches a random quote from the ZenQuotes API
     * @return Quote model for UI display
     */
    suspend fun getRandomQuote(): Result<Quote> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getRandomQuote()
                if (response.isNotEmpty()) {
                    val quoteResponse = response[0]
                    Result.success(
                        Quote(
                            text = quoteResponse.q,
                            author = quoteResponse.a
                        )
                    )
                } else {
                    Result.failure(Exception("No quotes received"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}