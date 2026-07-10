package com.example.klippr.notification.data.store

import com.example.klippr.notification.data.local.dao.NotificationDao
import com.example.klippr.notification.data.local.entity.NotificationEntity
import com.example.klippr.notification.data.mapper.NotificationTypeMapper
import com.example.klippr.notification.data.network.NotificationWebService
import com.example.klippr.notification.data.remote.dto.CreateNotificationBody
import com.example.klippr.notification.data.remote.dto.NotificationResource
import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID

/**
 * Store híbrido: Room como cache + API `/api/v1/Notifications` como fuente remota.
 * Los fallos de red no rompen el flujo local (UX offline-friendly).
 */
class NotificationStoreImpl(
    private val dao: NotificationDao,
    private val webService: NotificationWebService,
    private val gson: Gson = Gson(),
) : NotificationStore {

    override fun getAll(): Flow<List<Notification>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getUnreadCount(): Flow<Int> = dao.getUnreadCount()

    override suspend fun add(
        userId: String?,
        type: NotificationType,
        title: String,
        message: String,
        relatedId: String?,
    ) {
        var remoteId: String? = null
        if (!userId.isNullOrBlank()) {
            try {
                val created = webService.create(
                    CreateNotificationBody(
                        userId = userId,
                        type = NotificationTypeMapper.toApi(type),
                        title = title,
                        message = message,
                        relatedId = relatedId,
                    ),
                )
                remoteId = created.id?.takeIf { it.isNotBlank() }
            } catch (_: Exception) {
                // Sigue con insert local aunque falle el POST.
            }
        }

        dao.insert(
            NotificationEntity(
                id = remoteId ?: UUID.randomUUID().toString(),
                type = type.name,
                title = title,
                message = message,
                relatedId = relatedId,
                createdAt = System.currentTimeMillis(),
                isRead = false,
            ),
        )
    }

    override suspend fun syncFromRemote(userId: String) {
        if (userId.isBlank()) return
        val remote = try {
            parseNotificationList(webService.getByUser(userId, unreadOnly = false))
        } catch (_: Exception) {
            return
        }

        // Reemplazo simple del cache: limpia y reinserta el snapshot remoto.
        // Solo si el GET trajo datos o lista vacía explícita (no error).
        dao.deleteAll()
        remote.forEach { resource ->
            val entity = resource.toEntity() ?: return@forEach
            dao.insert(entity)
        }
    }

    private fun parseNotificationList(element: JsonElement): List<NotificationResource> {
        if (element.isJsonNull) return emptyList()
        if (element.isJsonArray) {
            val type = object : TypeToken<List<NotificationResource>>() {}.type
            return gson.fromJson(element, type) ?: emptyList()
        }
        if (element.isJsonObject) {
            val obj = element.asJsonObject
            for (key in listOf("items", "notifications", "data", "results")) {
                if (obj.has(key) && obj.get(key).isJsonArray) {
                    val type = object : TypeToken<List<NotificationResource>>() {}.type
                    return gson.fromJson(obj.get(key), type) ?: emptyList()
                }
            }
            // Objeto único.
            if (obj.has("id") || obj.has("notificationId")) {
                val one = gson.fromJson(obj, NotificationResource::class.java)
                return listOfNotNull(one)
            }
        }
        return emptyList()
    }

    override suspend fun markAsRead(id: String) {
        dao.markAsRead(id)
        try {
            webService.markAsRead(id)
        } catch (_: Exception) {
            // Cache local ya actualizado.
        }
    }

    override suspend fun markAllAsRead(userId: String?) {
        dao.markAllAsRead()
        if (userId.isNullOrBlank()) return
        try {
            webService.markAllAsRead(userId)
        } catch (_: Exception) {
            // Cache local ya actualizado.
        }
    }

    private fun NotificationResource.toEntity(): NotificationEntity? {
        val nid = id?.takeIf { it.isNotBlank() } ?: return null
        val createdMs = parseCreatedAt(createdAt) ?: System.currentTimeMillis()
        return NotificationEntity(
            id = nid,
            type = NotificationTypeMapper.fromApi(type).name,
            title = title?.takeIf { it.isNotBlank() } ?: "Notificación",
            message = message.orEmpty(),
            relatedId = relatedId,
            createdAt = createdMs,
            isRead = isRead == true,
        )
    }

    private fun parseCreatedAt(raw: String?): Long? {
        if (raw.isNullOrBlank()) return null
        raw.toLongOrNull()?.let { n ->
            return if (n > 1_000_000_000_000L) n else n * 1000L
        }
        return try {
            Instant.parse(raw).toEpochMilli()
        } catch (_: Exception) {
            null
        }
    }

    private fun NotificationEntity.toDomain() = Notification(
        id = id,
        type = runCatching { NotificationType.valueOf(type) }
            .getOrDefault(NotificationType.FAVORITE_ADDED),
        title = title,
        message = message,
        relatedId = relatedId,
        createdAt = createdAt,
        isRead = isRead,
    )
}
