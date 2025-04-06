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

val sampleWatchlistItems = listOf(
    WatchlistItem(
        symbol = "AAPL",
        companyName = "Apple Inc.",
        percentChange = 2.5f,
        logoUrl = "https://img.logo.dev/ticker/AAPL?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true",
        stockPrices = listOf(
            StockPricePoint("2024-04-01", 170.0f),
            StockPricePoint("2024-04-02", 172.3f),
            StockPricePoint("2024-04-03", 171.8f)
        )
    ),
    WatchlistItem(
        symbol = "GOOGL",
        companyName = "Alphabet Inc.",
        percentChange = -1.2f,
        logoUrl = "https://img.logo.dev/ticker/GOOGL?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true",
        stockPrices = listOf(
            StockPricePoint("2024-04-01", 2800.5f),
            StockPricePoint("2024-04-02", 2775.0f),
            StockPricePoint("2024-04-03", 2781.2f)
        )
    ),
    WatchlistItem(
        symbol = "MSFT",
        companyName = "Microsoft Corp.",
        percentChange = 0.8f,
        logoUrl = "https://img.logo.dev/ticker/MSFT?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true",
        stockPrices = listOf(
            StockPricePoint("2024-04-01", 310.0f),
            StockPricePoint("2024-04-02", 315.0f),
            StockPricePoint("2024-04-03", 312.8f)
        )
    ),
    WatchlistItem(
        symbol = "TSLA",
        companyName = "Tesla Inc.",
        percentChange = 5.3f,
        logoUrl = "https://img.logo.dev/ticker/TSLA?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true",
        stockPrices = listOf(
            StockPricePoint("2024-04-01", 690.0f),
            StockPricePoint("2024-04-02", 700.0f),
            StockPricePoint("2024-04-03", 705.0f)
        )
    )
)
