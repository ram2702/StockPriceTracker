package com.personal.realtimepricetracker.di

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.BuildConfig
import com.personal.realtimepricetracker.data.api.MockApi
import com.personal.realtimepricetracker.data.api.StockApi
import com.personal.realtimepricetracker.worker.PriceAlertWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStockApi(retrofit: Retrofit): StockApi {
        return retrofit.create(StockApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(app: Application): Context = app.applicationContext

}