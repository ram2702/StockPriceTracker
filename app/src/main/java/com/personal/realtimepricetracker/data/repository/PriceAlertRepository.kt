package com.personal.realtimepricetracker.data.repository

import com.personal.realtimepricetracker.data.db.PriceAlertDao
import com.personal.realtimepricetracker.data.db.PriceAlertEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceAlertRepository @Inject constructor(
    private val priceAlertDao: PriceAlertDao
) {
    fun insertPriceAlert(priceAlert: PriceAlertEntity) {
        priceAlertDao.insertPriceAlert(priceAlert)
    }

    fun deleteByWorkID(uuid: UUID){
        priceAlertDao.deletePriceAlertByWorkID(uuid)
    }

    fun getAlertPricesForTicker(ticker: String): Flow<List<Float>> {
        return priceAlertDao.getPriceAlertsForTicker(ticker)
    }

    fun fetchAllPriceAlerts(userID: String): Flow<List<PriceAlertEntity>> {
        return priceAlertDao.getAllPriceAlertsForUser(userID)
    }

    fun deletePriceAlert(priceAlert: PriceAlertEntity) {
        priceAlertDao.deletePriceAlert(priceAlert)
    }

    fun deleteAllPriceAlerts() {
        priceAlertDao.deleteAllPriceAlerts()
    }
}