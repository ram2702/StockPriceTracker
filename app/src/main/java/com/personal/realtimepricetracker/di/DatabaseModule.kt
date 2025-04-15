package com.personal.realtimepricetracker.di

import android.content.Context
import androidx.room.Room
import com.personal.realtimepricetracker.data.db.PriceAlertDao
import com.personal.realtimepricetracker.data.db.WatchListDao
import com.personal.realtimepricetracker.data.db.WatchListDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun dataBaseProvider(@ApplicationContext context: Context): WatchListDatabase {
        return Room.databaseBuilder(context, WatchListDatabase::class.java, "watchlist_db").fallbackToDestructiveMigration().build()
    }

    @Provides
    fun watchListDaoProvider(database: WatchListDatabase):WatchListDao{
        return database.WishListDao()
    }

    @Provides
    fun priceAlertDao(database: WatchListDatabase): PriceAlertDao {
        return database.PriceAlertDao()
    }
}