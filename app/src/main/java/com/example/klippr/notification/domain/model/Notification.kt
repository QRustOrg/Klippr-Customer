package com.example.klippr.notification.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val relatedId: String?,
    val createdAt: Long,
    val isRead: Boolean,
)