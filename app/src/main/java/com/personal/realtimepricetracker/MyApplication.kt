package com.personal.realtimepricetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.work.Configuration
import com.personal.realtimepricetracker.utils.Utils

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override val workManagerConfiguration: Configuration
        get() {
            Log.d("HiltWorkerFactory", "WorkerFactory injected: $workerFactory")
            return  Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "onCreate called")
        createNotificationChannel()
    }

    private fun  createNotificationChannel(){
        // Create channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Price Alerts"
            val descriptionText = "Alerts when stock hits your set price"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Utils.channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
