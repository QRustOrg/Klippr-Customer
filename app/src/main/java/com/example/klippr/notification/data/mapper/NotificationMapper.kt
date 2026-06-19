package com.example.klippr.notification.data.mapper

import com.example.klippr.notification.data.local.entity.NotificationEntity
import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType

fun NotificationEntity.toDomain() = Notification(
    id = id,
    type = NotificationType.valueOf(type),
    title = title,
    message = message,
    relatedId = relatedId,
    createdAt = createdAt,
    isRead = isRead,
)