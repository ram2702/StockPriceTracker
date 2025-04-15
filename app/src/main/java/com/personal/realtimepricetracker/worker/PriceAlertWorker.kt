package com.personal.realtimepricetracker.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.api.StockApi
import com.personal.realtimepricetracker.data.repository.PriceAlertRepository
import com.personal.realtimepricetracker.data.repository.WatchListRepository
import com.personal.realtimepricetracker.utils.Utils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class PriceAlertWorker @AssistedInject constructor (
     private val stockApi: StockApi,
     private val priceAlertRepository: PriceAlertRepository,
     @Assisted context: Context,
     @Assisted params: WorkerParameters
) : CoroutineWorker(context,params) {

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        val ticker = inputData.getString("ticker") ?: return Result.Failure()
        val alertPrice = inputData.getFloat("alertPrice", -1f)
        val alertType = inputData.getString("alertType")
        val companyName = inputData.getString("companyName")
        val refPrice = inputData.getFloat("currentPrice", -1f)
        try {
            val response = stockApi.getIntraDayPrices(symbol = ticker, apiKey = "NFB8GY61TW30BGGH")

            var currentPrice: Float = -1f
            if (response.isSuccessful) {
                response.body()?.let { stockResponse ->
                    Log.d("PriceAlertWorker","$stockResponse")
                    currentPrice = stockResponse.timeSeries.values.last().close
                }
            }
            Log.d("PriceAlertWorker", "checking isPriceAlertValid: ${isPriceAlertValid(refPrice, currentPrice, alertPrice)} and refPrice: $refPrice and alertPrice: $alertPrice currentPrice: ${currentPrice}")
            if (currentPrice != -1f && isPriceAlertValid(refPrice, currentPrice, alertPrice)) {
                showNotification(
                    context = applicationContext,
                    title = "Price Alert!",
                    message = "$companyName has reached ₹$currentPrice"
                )
                if(alertType.equals("Once")) {
                    WorkManager.getInstance(context = applicationContext).cancelWorkById(id)
                    priceAlertRepository.deleteByWorkID(id)
                    Log.d("PriceAlertWorker", "Price Alert worker $id for $companyName removed after hitting $currentPrice")
                }
            }
            return Result.Success()
        } catch (e: Exception) {
            Log.d("PriceAlertWorker", "Work failed with exception: $e")
            return Result.Failure()
        }
    }

    private fun isPriceAlertValid(
        refPrice: Float,
        currentPrice: Float,
        alertPrice: Float
    ): Boolean {
        return if (refPrice < alertPrice) currentPrice >= alertPrice else currentPrice <= alertPrice
    }

    private fun showNotification(context: Context, title: String, message: String) {

        val notification = NotificationCompat.Builder(context, Utils.channelId)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("PriceAlertWorker", "notification being sent.... ")
        manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }

}