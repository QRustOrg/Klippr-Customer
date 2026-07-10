package com.example.klippr.notification.data.store

import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationStore {
    fun getAll(): Flow<List<Notification>>
    fun getUnreadCount(): Flow<Int>

    /**
     * Inserta localmente y, si hay sesión, intenta POST al backend.
     * [userId] se usa para el POST remoto.
     */
    suspend fun add(
        userId: String?,
        type: NotificationType,
        title: String,
        message: String,
        relatedId: String?,
    )

    /** Sincroniza el inbox remoto hacia Room (cache local). */
    suspend fun syncFromRemote(userId: String)

    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead(userId: String?)
}
