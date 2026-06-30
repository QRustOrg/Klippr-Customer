package com.example.klippr.notification.data.store

import com.example.klippr.notification.data.local.dao.NotificationDao
import com.example.klippr.notification.data.local.entity.NotificationEntity
import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementacion de [NotificationStore] sobre Room (fuente local).
 * El mapeo entidad -> dominio se hace en linea.
 */
class NotificationStoreImpl(
    private val dao: NotificationDao,
) : NotificationStore {

    override fun getAll(): Flow<List<Notification>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getUnreadCount(): Flow<Int> = dao.getUnreadCount()

    override suspend fun add(type: NotificationType, title: String, message: String, relatedId: String?) {
        dao.insert(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                type = type.name,
                title = title,
                message = message,
                relatedId = relatedId,
                createdAt = System.currentTimeMillis(),
                isRead = false,
            ),
        )
    }

    override suspend fun markAsRead(id: String) = dao.markAsRead(id)

    override suspend fun markAllAsRead() = dao.markAllAsRead()

    private fun NotificationEntity.toDomain() = Notification(
        id = id,
        type = NotificationType.valueOf(type),
        title = title,
        message = message,
        relatedId = relatedId,
        createdAt = createdAt,
        isRead = isRead,
    )
}
