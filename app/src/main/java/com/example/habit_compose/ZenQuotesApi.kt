package com.example.habit_compose


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Retrofit interface for ZenQuotes API
 */
interface ZenQuotesApi {
    @GET("random")
    suspend fun getRandomQuote(): List<QuoteResponse>

    companion object {
        private const val BASE_URL = "https://zenquotes.io/api/"

        fun create(): ZenQuotesApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ZenQuotesApi::class.java)
        }
    }
}