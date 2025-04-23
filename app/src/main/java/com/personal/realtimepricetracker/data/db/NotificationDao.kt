package com.personal.realtimepricetracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notificationEntity: NotificationEntity) :Long

    @Query("SELECT * FROM worker_notification WHERE uid= :uid")
    fun getAllNotificationItems(uid: String): Flow<List<NotificationEntity>>

    @Query("DELETE FROM worker_notification WHERE notificationId=:notificationId")
    fun deleteNotificationById(notificationId:Int)

    @Query("DELETE FROM worker_notification")
    fun deleteAllNotifications()
}