package com.personal.realtimepricetracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.personal.realtimepricetracker.data.db.WatchListDao
import com.personal.realtimepricetracker.data.db.WatchListDatabase
import com.personal.realtimepricetracker.data.db.WatchListEntity
import com.personal.realtimepricetracker.data.model.DailyData
import com.personal.realtimepricetracker.data.model.StockPricePoint
import com.personal.realtimepricetracker.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchListRepository @Inject constructor(
    private val watchListDao: WatchListDao
) {
    val watchlistSymbols: Flow<List<WatchListEntity>> = watchListDao.getAllWatchListTickers()

    fun insertToWatchList(symbol: String, name: String, stockPricePoints: Map<String,DailyData>) {
        val percentageChange = (stockPricePoints.values.last().close-stockPricePoints.values.first().close)/stockPricePoints.values.first().close*100
        watchListDao.insertWatchListItem(
            WatchListEntity(
                ticker = symbol,
                companyName = name,
                userID = FirebaseAuth.getInstance().uid.toString(),
                percentageChange = percentageChange,
                stockPricePoints = stockPricePoints
            )
        )

    }
}