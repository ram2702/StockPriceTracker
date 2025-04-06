package com.personal.realtimepricetracker.data.model

data class StockPricePoint(
    val date: String,
    val close: Float
)

data class WatchlistItem(
    val symbol: String,
    val stockPrices: List<StockPricePoint>,
    val percentChange: Float,
    val logoUrl:String,
    val companyName: String
)

data class IndexSample(
    val indexName: String,
    val companyName: String,
    val stockPrices: List<StockPricePoint>
)

val majorGlobalIndices = listOf("SPY", "QQQ", "DIA", "IWM")

val sampleIndexData = listOf(
    IndexSample(
        indexName = "AMZN",
        companyName = "Amazon, Inc",
        stockPrices = listOf(
            StockPricePoint("2024-04-01", 152.4f),
            StockPricePoint("2024-04-02", 153.0f),
            StockPricePoint("2024-04-03", 154.8f),
            StockPricePoint("2024-04-04", 153.2f),
            StockPricePoint("2024-04-05", 154.1f)
        )
    ),
    IndexSample(
        indexName = "GOOG",
        companyName = "Alphabet Inc",
        stockPrices = listOf(
            StockPricePoint("2024-04-01", 2750.5f),
            StockPricePoint("2024-04-02", 2765.0f),
            StockPricePoint("2024-04-03", 2745.8f),
            StockPricePoint("2024-04-04", 2780.2f),
            StockPricePoint("2024-04-05", 2790.1f)
        )
    )
    // Add more entries if needed
)