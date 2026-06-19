package com.example.klippr.notification.data.repository

import com.example.klippr.notification.data.local.dao.NotificationDao
import com.example.klippr.notification.data.local.entity.NotificationEntity
import com.example.klippr.notification.data.mapper.toDomain
import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class NotificationRepositoryImpl(
    private val dao: NotificationDao,
) : NotificationRepository {

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
}