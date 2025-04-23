package com.personal.realtimepricetracker;

import android.content.BroadcastReceiver;
import android.content.Context
import android.content.Intent
import android.util.Log
import com.personal.realtimepricetracker.data.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDeleteReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val alertPrice = intent.getFloatExtra(EXTRA_ALERT_PRICE, -1f)
        val stockSymbol = intent.getStringExtra(EXTRA_STOCK_SYMBOL) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.deleteNotificationFromIntent(notificationId)
            Log.d("NotificationDeleteReceiver", "Notification dismissed for $notificationId $stockSymbol $alertPrice")
        }
    }

    companion object {
        const val EXTRA_NOTIFICATION_ID = "ALERT_PRICE"
        const val EXTRA_STOCK_SYMBOL = "extra_stock_symbol"
        const val EXTRA_ALERT_PRICE = "extra_alert_price"
    }
}
