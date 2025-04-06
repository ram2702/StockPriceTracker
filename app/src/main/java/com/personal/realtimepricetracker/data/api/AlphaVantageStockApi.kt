package com.personal.realtimepricetracker.data.api

import com.personal.realtimepricetracker.data.model.SearchTickerResponse
import com.personal.realtimepricetracker.data.model.StockResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET("query?function=TIME_SERIES_DAILY")
    suspend fun getDailyPrices(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): Response<StockResponse>

    @GET("query?function=SYMBOL_SEARCH")
    suspend fun getTickerFromCompanyName(
        @Query("keywords") keyword: String,
        @Query("apikey") apiKey: String
    ): Response<SearchTickerResponse>
}

