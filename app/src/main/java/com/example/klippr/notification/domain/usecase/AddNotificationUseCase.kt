package com.example.klippr.notification.domain.usecase

import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.domain.repository.NotificationRepository

class AddNotificationUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(
        type: NotificationType,
        title: String,
        message: String,
        relatedId: String? = null,
    ) = repository.add(type, title, message, relatedId)
}