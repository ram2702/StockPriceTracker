package com.personal.realtimepricetracker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.work.Data
import java.util.UUID

@Entity(tableName = "price_alert")
class PriceAlertEntity(
    @PrimaryKey val workId: UUID,
    @ColumnInfo val ticker: String,
    @ColumnInfo val companyName: String,
    @ColumnInfo val userID: String,
    @ColumnInfo val currentPrice: Float,
    @ColumnInfo val alertPrice: Float,
    @ColumnInfo val alertType: String,
    @ColumnInfo val alertStatus: Boolean
)

fun PriceAlertEntity.toWorkData(): Data {
    return Data.Builder()
        .putString("ticker", ticker)
        .putFloat("alertPrice", alertPrice)
        .putFloat("currentPrice", currentPrice)
        .putString("alertType", alertType)
        .putBoolean("alertStatus", alertStatus)
        .putString("companyName", companyName)
        .putString("userID", userID)
        .build()
}
