package com.personal.realtimepricetracker.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class StockResponse(
    @SerializedName("Meta Data")
    val metadata: Map<String,String>,
    @SerializedName(
        value = "Time Series (Daily)",
        alternate = [
            "Time Series (5min)",
            "Weekly Time Series",
            "Monthly Time Series"
        ]
    )
    val timeSeries: Map<String, DailyData>
)
@Serializable
data class DailyData(
    @SerializedName("1. open") val open: Float,
    @SerializedName("2. high") val high: Float,
    @SerializedName("3. low") val low: Float,
    @SerializedName("4. close") val close: Float,
    @SerializedName("5. volume") val volume: Long
)

data class SearchTickerResponse(
    @SerializedName("bestMatches")
    val bestMatches : List<SearchData>
)


data class SearchData(
    @SerializedName("1. symbol") val ticker: String,
    @SerializedName("2. name") val name: String,
    @SerializedName("3. type") val type: String,
    @SerializedName("4. region") val region: String,
    @SerializedName("5. marketOpen") val marketOpen: String,
    @SerializedName("6. marketClose") val marketClose: String,
    @SerializedName("7. timezone") val timezone: String,
    @SerializedName("8. currency") val currency: String,
    @SerializedName("9. matchScore") val matchScore: String
)
