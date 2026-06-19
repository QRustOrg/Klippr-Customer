package com.example.klippr.notification.domain.repository

import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAll(): Flow<List<Notification>>
    fun getUnreadCount(): Flow<Int>
    suspend fun add(type: NotificationType, title: String, message: String, relatedId: String?)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
}