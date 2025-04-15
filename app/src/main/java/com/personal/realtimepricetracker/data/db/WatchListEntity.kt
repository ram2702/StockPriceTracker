package com.personal.realtimepricetracker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.realtimepricetracker.data.model.DailyData

@Entity(tableName = "watchlist")
data class WatchListEntity (
    @PrimaryKey @ColumnInfo val ticker: String,
    @ColumnInfo val companyName: String,
    @ColumnInfo val userID: String,
    @ColumnInfo val percentageChange: Float,
    @ColumnInfo val stockPricePoints: Map<String, DailyData>
)