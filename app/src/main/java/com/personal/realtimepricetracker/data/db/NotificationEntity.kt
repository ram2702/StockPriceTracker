package com.personal.realtimepricetracker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worker_notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo val notificationId: Int = 0,
    @ColumnInfo val companyName: String,
    @ColumnInfo val ticker: String,
    @ColumnInfo val alertPrice: Float,
    @ColumnInfo val description: String,
    @ColumnInfo val isRead: Boolean,
    @ColumnInfo val uid: String,
    @ColumnInfo val time: Long
    )