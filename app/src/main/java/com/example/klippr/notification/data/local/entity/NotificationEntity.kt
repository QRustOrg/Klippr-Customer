package com.example.klippr.notification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val message: String,
    val relatedId: String?,
    val createdAt: Long,
    val isRead: Boolean,
)