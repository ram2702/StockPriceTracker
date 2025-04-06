package com.personal.realtimepricetracker.data.repository

import com.personal.realtimepricetracker.data.db.WatchListDao
import com.personal.realtimepricetracker.data.db.WatchListDatabase
import com.personal.realtimepricetracker.data.db.WatchListEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchListRepository @Inject constructor(
    private val watchListDao: WatchListDao
) {
    val watchlistSymbols: Flow<List<WatchListEntity>> = watchListDao.getAllWatchListTickers()

    fun insertToWatchList(symbol: String, name: String) {
        watchListDao.insertWatchListItem(WatchListEntity(symbol,name))
    }
}