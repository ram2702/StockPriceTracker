package com.personal.realtimepricetracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WatchListEntity::class], version = 1)
abstract class WatchListDatabase: RoomDatabase(){
    abstract fun WishListDao(): WatchListDao
}
