package com.example.habit_compose

import android.content.Context
import android.content.Intent

/**
 * Utility class to handle sharing functionality
 */
object ShareUtils {

    /**
     * Shares the given quote text using Android's native sharing intent
     *
     * @param context Android context
     * @param quote The quote to share
     * @param author The author of the quote
     */
    fun shareQuote(context: Context, quote: String, author: String) {
        val shareText = "\"$quote\" - $author"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Quote")
        context.startActivity(shareIntent)
    }
}