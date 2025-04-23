package com.personal.realtimepricetracker.worker

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.auth.FirebaseAuth
import com.personal.realtimepricetracker.NotificationDeleteReceiver
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.data.api.StockApi
import com.personal.realtimepricetracker.data.db.NotificationEntity
import com.personal.realtimepricetracker.data.repository.NotificationRepository
import com.personal.realtimepricetracker.data.repository.PriceAlertRepository
import com.personal.realtimepricetracker.utils.Utils
import com.personal.realtimepricetracker.viewmodel.MainViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PriceAlertWorker @AssistedInject constructor (
     private val stockApi: StockApi,
     private val priceAlertRepository: PriceAlertRepository,
     private val notificationRepository: NotificationRepository,
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
                val notificationId = notificationRepository.insertNotificationItem(NotificationEntity(
                    companyName = companyName!!,
                    ticker = ticker,
                    description = "$ticker has reached $$currentPrice",
                    isRead = false,
                    alertPrice = alertPrice,
                    uid = FirebaseAuth.getInstance().uid.toString(),
                    time = System.currentTimeMillis()
                ))
                showNotification(
                    context = applicationContext,
                    title = "Alert for $companyName",
                    message = "$ticker has reached $$currentPrice",
                    ticker,
                    notificationId,
                    alertPrice
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

    private suspend fun showNotification(
        context: Context,
        title: String,
        message: String,
        ticker: String,
        notificationId: Int,
        alertPrice: Float
    ) {
        val deleteIntent : PendingIntent = createDeleteIntent(context,notificationId,alertPrice,ticker)

        val bitmap = loadBitmapFromUrl(context, Utils.getLogoUrlFromTicker(ticker))

        val notification = NotificationCompat.Builder(context, Utils.channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(message)
            .setDeleteIntent(deleteIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("PriceAlertWorker", "notification being sent.... ")
        manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }

    fun createDeleteIntent(
        context: Context,
        notificationId: Int,
        alertPrice: Float,
        stockSymbol: String
    ): PendingIntent {

        val intent = Intent(context, NotificationDeleteReceiver::class.java).apply {
            putExtra(NotificationDeleteReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(NotificationDeleteReceiver.EXTRA_ALERT_PRICE, alertPrice)
            putExtra(NotificationDeleteReceiver.EXTRA_STOCK_SYMBOL, stockSymbol)
        }
        Log.d("PriceAlertWorker", "createDeleteIntent: $notificationId $stockSymbol $alertPrice")
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    suspend fun loadBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        val result = context.imageLoader.execute(request)
        return if (result is SuccessResult) {
            (result.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    }
}
