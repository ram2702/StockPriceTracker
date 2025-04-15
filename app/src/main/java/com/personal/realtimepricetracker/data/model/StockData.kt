package com.personal.realtimepricetracker.data.model

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class StockData(
    val ticker: String,
    val companyName: String,
    val stockPrices: Map<String, DailyData>,
    val percentChange: Float,
    val logoUrl: String,
)

@Serializable
data class StockPricePoint(
    val date: String,
    val close: Float
)

val majorGlobalIndices = listOf(
    "SPY" to "S&P 500",
    "QQQ" to "Nasdaq-100",
    "DIA" to "Dow Jones Industrial Average",
    "IWM" to "Russell 2000"
)

val sampleIndexData = listOf(
    StockData(
        ticker = "SPY",
        companyName = "S&P 500",
        stockPrices = mapOf(
            "2024-04-10" to DailyData(435.2f, 440.1f, 432.0f, 438.9f, 72000000),
            "2024-04-09" to DailyData(430.5f, 436.0f, 428.3f, 434.7f, 68000000),
            "2024-04-08" to DailyData(425.8f, 431.2f, 423.9f, 430.1f, 70500000),
            "2024-04-05" to DailyData(422.0f, 426.5f, 419.5f, 424.0f, 67000000),
            "2024-04-04" to DailyData(418.7f, 423.4f, 417.2f, 421.9f, 69000000),
            "2024-04-03" to DailyData(420.0f, 422.8f, 416.0f, 418.3f, 66000000),
            "2024-04-02" to DailyData(415.3f, 419.0f, 412.5f, 417.7f, 64500000)
        ),
        percentChange = 0.67f,
        logoUrl = "https://img.logo.dev/ticker/SPY?token=pk_xyz&retina=true"
    ),
    StockData(
        ticker = "QQQ",
        companyName = "Nasdaq-100",
        stockPrices = mapOf(
            "2024-04-10" to DailyData(435.2f, 440.1f, 432.0f, 438.9f, 72000000),
            "2024-04-09" to DailyData(430.5f, 436.0f, 428.3f, 434.7f, 68000000),
            "2024-04-08" to DailyData(425.8f, 431.2f, 423.9f, 430.1f, 70500000),
            "2024-04-05" to DailyData(422.0f, 426.5f, 419.5f, 424.0f, 67000000),
            "2024-04-04" to DailyData(418.7f, 423.4f, 417.2f, 421.9f, 69000000),
            "2024-04-03" to DailyData(420.0f, 422.8f, 416.0f, 418.3f, 66000000),
            "2024-04-02" to DailyData(415.3f, 419.0f, 412.5f, 417.7f, 64500000)
        ),
        percentChange = 1.12f,
        logoUrl = "https://img.logo.dev/ticker/QQQ?token=pk_xyz&retina=true"
    ),
    StockData(
        ticker = "DIA",
        companyName = "Dow Jones Industrial Average",
        stockPrices = mapOf(
            "2024-04-10" to DailyData(435.2f, 440.1f, 432.0f, 438.9f, 72000000),
            "2024-04-09" to DailyData(430.5f, 436.0f, 428.3f, 434.7f, 68000000),
            "2024-04-08" to DailyData(425.8f, 431.2f, 423.9f, 430.1f, 70500000),
            "2024-04-05" to DailyData(422.0f, 426.5f, 419.5f, 424.0f, 67000000),
            "2024-04-04" to DailyData(418.7f, 423.4f, 417.2f, 421.9f, 69000000),
            "2024-04-03" to DailyData(420.0f, 422.8f, 416.0f, 418.3f, 66000000),
            "2024-04-02" to DailyData(415.3f, 419.0f, 412.5f, 417.7f, 64500000)
        ),
        percentChange = 0.42f,
        logoUrl = "https://img.logo.dev/ticker/DIA?token=pk_xyz&retina=true"
    ),
    StockData(
        ticker = "IWM",
        companyName = "Russell 2000",
        stockPrices = mapOf(
            "2024-04-10" to DailyData(435.2f, 440.1f, 432.0f, 438.9f, 72000000),
            "2024-04-09" to DailyData(430.5f, 436.0f, 428.3f, 434.7f, 68000000),
            "2024-04-08" to DailyData(425.8f, 431.2f, 423.9f, 430.1f, 70500000),
            "2024-04-05" to DailyData(422.0f, 426.5f, 419.5f, 424.0f, 67000000),
            "2024-04-04" to DailyData(418.7f, 423.4f, 417.2f, 421.9f, 69000000),
            "2024-04-03" to DailyData(420.0f, 422.8f, 416.0f, 418.3f, 66000000),
            "2024-04-02" to DailyData(415.3f, 419.0f, 412.5f, 417.7f, 64500000)
        ),
        percentChange = 0.95f,
        logoUrl = "https://img.logo.dev/ticker/IWM?token=pk_xyz&retina=true"
    )
)

val sampleWatchlistItems = listOf(
    StockData(
        ticker = "AAPL",
        companyName = "Apple Inc.",
        stockPrices = mapOf(
            "2025-04-10" to DailyData(172.50f, 175.00f, 171.30f, 174.20f, 78200000)
        ),
        percentChange = 1.28f,
        logoUrl = "https://img.logo.dev/ticker/AAPL?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true"
    ),
    StockData(
        ticker = "GOOGL",
        companyName = "Alphabet Inc.",
        stockPrices = mapOf(
            "2025-04-10" to DailyData(2800.00f, 2850.00f, 2790.00f, 2842.50f, 1500000)
        ),
        percentChange = 1.51f,
        logoUrl = "https://img.logo.dev/ticker/GOOGL?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true"
    ),
    StockData(
        ticker = "AMZN",
        companyName = "Amazon.com, Inc.",
        stockPrices = mapOf(
            "2025-04-10" to DailyData(3200.00f, 3245.00f, 3180.00f, 3230.75f, 4200000)
        ),
        percentChange = 2.13f,
        logoUrl = "https://img.logo.dev/ticker/AMZN?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true"
    ),
    StockData(
        ticker = "NFLX",
        companyName = "Netflix, Inc.",
        stockPrices = mapOf(
            "2025-04-10" to DailyData(480.00f, 495.00f, 478.00f, 491.60f, 5100000)
        ),
        percentChange = 2.05f,
        logoUrl = "https://img.logo.dev/ticker/NFLX?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true"
    ),
    StockData(
        ticker = "NVDA",
        companyName = "NVIDIA Corporation",
        stockPrices = mapOf(
            "2025-04-10" to DailyData(875.00f, 890.00f, 870.00f, 886.70f, 27000000)
        ),
        percentChange = 1.87f,
        logoUrl = "https://img.logo.dev/ticker/NVDA?token=pk_czwzG--yTyqlZnf3x1hvLw&retina=true"
    )
)
