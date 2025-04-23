package com.personal.realtimepricetracker.utils

import com.personal.realtimepricetracker.data.model.DailyData
import com.personal.realtimepricetracker.data.model.StockData
import com.personal.realtimepricetracker.data.model.StockPricePoint

object Utils{
    val channelId = "price_alert_channel"
    fun sortWatchlistItems(
        items: List<StockData>,
        filter: Int
    ): List<StockData> {
        return when (filter) {
            1 -> items.sortedByDescending { it.percentChange } // Gainers Asc
            2 -> items.sortedBy { it.percentChange }           // Gainers Desc
            3 -> items.sortedBy { it.stockPrices.values.lastOrNull()?.close ?: 0f }  // Price Asc
            4 -> items.sortedByDescending { it.stockPrices.values.lastOrNull()?.close ?: 0f } // Price Desc
            else -> items // No sorting or default
        }
    }

    fun getStockPricePoints(item: Map<String, DailyData>) = item.map { (date, dailyData) -> StockPricePoint(date, dailyData.close) }

    fun getLogoUrlFromTicker(ticker: String) =
        "https://img.logo.dev/ticker/$ticker?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true"

    fun formatRelativeTime(timeInMillis: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - timeInMillis

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "${seconds}s ago"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            weeks < 4 -> "${weeks}w ago"
            months < 12 -> "${months}mo ago"
            else -> "${years}y ago"
        }
    }
    fun extractLastWord(text: String): String {
        val words = text.trim().split(" ")
        return if (words.isNotEmpty()) words.last() else ""
    }
}