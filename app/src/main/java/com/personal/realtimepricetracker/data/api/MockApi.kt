package com.personal.realtimepricetracker.data.api

import com.personal.realtimepricetracker.data.model.DailyData
import com.personal.realtimepricetracker.data.model.SearchData
import com.personal.realtimepricetracker.data.model.SearchTickerResponse
import com.personal.realtimepricetracker.data.model.StockResponse
import retrofit2.Response
import retrofit2.http.GET

class MockApi : StockApi {
    override suspend fun getIntraDayPrices(
        symbol: String,
        interval: String,
        apiKey: String
    ): Response<StockResponse> {
        val mockMetadata = mapOf(
            "1. Information" to "Intraday (5min) open, high, low, close prices and volume",
            "2. Symbol" to "IBM",
            "3. Last Refreshed" to "2025-04-11 19:55:00",
            "4. Interval" to "5min",
            "5. Output Size" to "Compact",
            "6. Time Zone" to "US/Eastern"
        )

        val mockTimeSeries = mapOf(
            "2025-04-11 19:55:00" to DailyData(
                open = 235.8800f,
                high = 235.9800f,
                low = 235.8800f,
                close = 235.9800f,
                volume = 8
            ),
            "2025-04-11 19:50:00" to DailyData(
                open = 235.5000f,
                high = 235.5000f,
                low = 235.5000f,
                close = 235.5000f,
                volume = 25
            ),
            "2025-04-11 19:45:00" to DailyData(
                open = 235.0100f,
                high = 235.0100f,
                low = 235.0100f,
                close = 235.0100f,
                volume = 41
            ),
            "2025-04-11 19:20:00" to DailyData(
                open = 235.9900f,
                high = 235.9900f,
                low = 235.0100f,
                close = 235.0100f,
                volume = 22
            )
        )

        val mockResponse = StockResponse(
            metadata = mockMetadata,
            timeSeries = mockTimeSeries
        )

        return Response.success(mockResponse)
    }


    @GET("query?function=TIME_SERIES_DAILY")
    override suspend fun getDailyPrices(symbol: String, apiKey: String): Response<StockResponse> {
        val mockMetaData = mapOf(
            "1. Information" to "Mock Time Series (Daily)",
            "2. Symbol" to symbol,
            "3. Last Refreshed" to "2025-04-10",
            "4. Output Size" to "Compact",
            "5. Time Zone" to "US/Eastern"
        )

        val mockTimeSeries = mapOf(
            "2025-04-10" to DailyData(
                open = 150.0f,
                high = 155.0f,
                low = 149.0f,
                close = 154.0f,
                volume = 1200000L
            ),
            "2025-04-09" to DailyData(
                open = 145.0f,
                high = 150.0f,
                low = 144.0f,
                close = 149.5f,
                volume = 980000L
            ),
            "2025-04-10" to DailyData(
                open = 150.0f,
                high = 155.0f,
                low = 149.0f,
                close = 154.0f,
                volume = 1200000L
            ),
            "2025-04-09" to DailyData(
                open = 145.0f,
                high = 150.0f,
                low = 144.0f,
                close = 149.5f,
                volume = 980000L
            ),
            "2025-04-10" to DailyData(
                open = 150.0f,
                high = 155.0f,
                low = 149.0f,
                close = 154.0f,
                volume = 1200000L
            ),
            "2025-04-09" to DailyData(
                open = 145.0f,
                high = 150.0f,
                low = 144.0f,
                close = 149.5f,
                volume = 980000L
            )
        )

        val mockResponse = StockResponse(
            metadata = mockMetaData,
            timeSeries = mockTimeSeries
        )

        return Response.success(mockResponse)
    }

    override suspend fun getWeeklyPrices(symbol: String, apiKey: String): Response<StockResponse> {
        val mockMetadata = mapOf(
            "1. Information" to "Weekly Prices (open, high, low, close) and Volumes",
            "2. Symbol" to symbol,
            "3. Last Refreshed" to "2025-04-11",
            "4. Time Zone" to "US/Eastern"
        )

        val mockTimeSeries = mapOf(
            "2025-04-11" to DailyData(
                open = 234.5f,
                high = 237.2f,
                low = 232.0f,
                close = 235.8f,
                volume = 1120000
            ),
            "2025-04-04" to DailyData(
                open = 229.0f,
                high = 234.0f,
                low = 228.0f,
                close = 233.2f,
                volume = 985000
            ),
            "2025-03-28" to DailyData(
                open = 225.5f,
                high = 230.1f,
                low = 224.0f,
                close = 229.3f,
                volume = 1043000
            ),
            "2025-03-21" to DailyData(
                open = 220.0f,
                high = 226.5f,
                low = 219.2f,
                close = 225.7f,
                volume = 970000
            )
        )

        val mockResponse = StockResponse(
            metadata = mockMetadata,
            timeSeries = mockTimeSeries
        )

        return Response.success(mockResponse)
    }


    override suspend fun getMonthlyPrices(symbol: String, apiKey: String): Response<StockResponse> {
        val mockMetadata = mapOf(
            "1. Information" to "Monthly Prices (open, high, low, close) and Volumes",
            "2. Symbol" to symbol,
            "3. Last Refreshed" to "2025-04-11",
            "4. Time Zone" to "US/Eastern"
        )

        val mockTimeSeries = mapOf(
            "2025-04-01" to DailyData(
                open = 230.0f,
                high = 238.0f,
                low = 228.0f,
                close = 235.5f,
                volume = 4200000
            ),
            "2025-03-01" to DailyData(
                open = 222.0f,
                high = 233.0f,
                low = 220.5f,
                close = 230.0f,
                volume = 3950000
            ),
            "2025-02-01" to DailyData(
                open = 215.0f,
                high = 225.0f,
                low = 212.0f,
                close = 222.8f,
                volume = 4100000
            ),
            "2025-01-01" to DailyData(
                open = 210.0f,
                high = 216.5f,
                low = 208.0f,
                close = 214.2f,
                volume = 3780000
            )
        )

        val mockResponse = StockResponse(
            metadata = mockMetadata,
            timeSeries = mockTimeSeries
        )

        return Response.success(mockResponse)
    }


    override suspend fun getTickerFromCompanyName(
        keyword: String,
        apiKey: String
    ): Response<SearchTickerResponse> {
        return Response.success(
            SearchTickerResponse( listOf(
                    SearchData(
                        ticker = "AMZN",
                        name = "Amazon.com Inc.",
                        type = "Equity",
                        region = "United States",
                        marketOpen = "09:30",
                        marketClose = "16:00",
                        timezone = "UTC-05",
                        currency = "USD",
                        matchScore = "0.8700"
                    ),
                SearchData(
                    ticker = "NFLX",
                    name = "Netflix Inc.",
                    type = "Equity",
                    region = "United States",
                    marketOpen = "09:30",
                    marketClose = "16:00",
                    timezone = "UTC-05",
                    currency = "USD",
                    matchScore = "0.8500"
                ),
                SearchData(
                    ticker = "NVDA",
                    name = "NVIDIA Corporation",
                    type = "Equity",
                    region = "United States",
                    marketOpen = "09:30",
                    marketClose = "16:00",
                    timezone = "UTC-05",
                    currency = "USD",
                    matchScore = "0.8400"
                ),
                SearchData(
                    ticker = "RELIANCE.BSE",
                    name = "Reliance Industries Limited",
                    type = "Equity",
                    region = "India",
                    marketOpen = "09:15",
                    marketClose = "15:30",
                    timezone = "UTC+05:30",
                    currency = "INR",
                    matchScore = "0.8200"
                ),
                SearchData(
                    ticker = "TCS.BSE",
                    name = "Tata Consultancy Services Limited",
                    type = "Equity",
                    region = "India",
                    marketOpen = "09:15",
                    marketClose = "15:30",
                    timezone = "UTC+05:30",
                    currency = "INR",
                    matchScore = "0.8100"
                ),
                SearchData(
                    ticker = "META",
                    name = "Meta Platforms Inc.",
                    type = "Equity",
                    region = "United States",
                    marketOpen = "09:30",
                    marketClose = "16:00",
                    timezone = "UTC-05",
                    currency = "USD",
                    matchScore = "0.8000"
                ),
                SearchData(
                    ticker = "BABA",
                    name = "Alibaba Group Holding Limited",
                    type = "Equity",
                    region = "China",
                    marketOpen = "09:30",
                    marketClose = "15:00",
                    timezone = "UTC+08",
                    currency = "CNY",
                    matchScore = "0.7900"
                ),
                SearchData(
                    ticker = "SONY",
                    name = "Sony Group Corporation",
                    type = "Equity",
                    region = "Japan",
                    marketOpen = "09:00",
                    marketClose = "15:00",
                    timezone = "UTC+09",
                    currency = "JPY",
                    matchScore = "0.7700"
                ),
                SearchData(
                    ticker = "SAP.DE",
                    name = "SAP SE",
                    type = "Equity",
                    region = "Germany",
                    marketOpen = "09:00",
                    marketClose = "17:30",
                    timezone = "UTC+01",
                    currency = "EUR",
                    matchScore = "0.7600"
                ),
                SearchData(
                    ticker = "SHOP",
                    name = "Shopify Inc.",
                    type = "Equity",
                    region = "Canada",
                    marketOpen = "09:30",
                    marketClose = "16:00",
                    timezone = "UTC-05",
                    currency = "CAD",
                    matchScore = "0.7500"
                )
            )

        ))
    }
}