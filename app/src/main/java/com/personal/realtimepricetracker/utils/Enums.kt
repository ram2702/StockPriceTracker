package com.personal.realtimepricetracker.utils

import com.personal.realtimepricetracker.ui.composables.TimeRange

enum class SearchDataIndex(val index: Int) {
    SYMBOL(0),
    NAME(1),
    TYPE(2),
    REGION(3),
    MARKET_OPEN(4),
    MARKET_CLOSE(5),
    TIMEZONE(6),
    CURRENCY(7)
}

enum class ApiEndPoints(val functionString: String){
    TIME_SERIES_INTRADAY("TIME_SERIES_INTRADAY"),
    TIME_SERIES_DAILY("TIME_SERIES_DAILY"),
    TIME_SERIES_WEEKLY("TIME_SERIES_WEEKLY"),
    TIME_SERIES_MONTHLY("TIME_SERIES_MONTHLY")
}

val filterOptions: List<TimeRange> = listOf(
    TimeRange.IntraDay, TimeRange.Daily, TimeRange.Weekly, TimeRange.Monthly
)