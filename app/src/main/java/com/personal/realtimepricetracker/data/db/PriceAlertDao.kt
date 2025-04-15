package com.personal.realtimepricetracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.google.common.base.Ticker
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PriceAlertDao {
    @Insert
    fun insertPriceAlert(priceAlertEntity: PriceAlertEntity)

    @Query("SELECT * FROM price_alert")
    fun getAllPriceAlerts(): Flow<List<PriceAlertEntity>>

    @Query("DELETE FROM price_alert WHERE workId = :id")
    fun deletePriceAlert(id: UUID)

    @Query("SELECT alertPrice FROM price_alert WHERE ticker = :ticker")
    fun getPriceAlertsForTicker(ticker: String) : Flow<List<Float>>
}
