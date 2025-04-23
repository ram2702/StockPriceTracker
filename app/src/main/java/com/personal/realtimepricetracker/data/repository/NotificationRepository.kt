package com.personal.realtimepricetracker.data.repository

import android.util.Log
import com.personal.realtimepricetracker.data.db.NotificationDao
import com.personal.realtimepricetracker.data.db.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {

    fun getAllNotificationsForUser(uid:String) : Flow<List<NotificationEntity>> =
        notificationDao.getAllNotificationItems(uid)

    fun insertNotificationItem(notificationEntity: NotificationEntity):Int {
       val id =  notificationDao.insertNotification(notificationEntity)
       Log.d("NotificationRepository", "id: $id")
       return id.toInt()
    }

    fun deleteNotificationFromIntent(notificationId: Int) {
        Log.d("NotificationRepository","Deleting notification item for $notificationId")
        notificationDao.deleteNotificationById(notificationId)
    }

    fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }
}