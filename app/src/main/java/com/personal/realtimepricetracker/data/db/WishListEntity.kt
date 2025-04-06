package com.personal.realtimepricetracker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchListEntity (
    @PrimaryKey @ColumnInfo val ticker: String,
    @ColumnInfo val companyName: String
)