package com.personal.realtimepricetracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [WatchListEntity::class, PriceAlertEntity::class], exportSchema = true ,version = 7)
@TypeConverters(Converters::class)
abstract class WatchListDatabase: RoomDatabase(){
    abstract fun WishListDao(): WatchListDao
    abstract fun PriceAlertDao(): PriceAlertDao
}
