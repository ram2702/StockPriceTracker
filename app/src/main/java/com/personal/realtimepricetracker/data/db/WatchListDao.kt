package com.personal.realtimepricetracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWatchListItem(watchListEntity: WatchListEntity)

    @Query("SELECT * FROM watchlist")
    fun getAllWatchListTickers(): Flow<List<WatchListEntity>>
}