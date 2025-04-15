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
}