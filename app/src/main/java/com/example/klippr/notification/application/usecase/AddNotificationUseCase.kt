package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.data.store.NotificationStore

class AddNotificationUseCase(private val repository: NotificationStore) {
    suspend operator fun invoke(
        type: NotificationType,
        title: String,
        message: String,
        relatedId: String? = null,
    ) = repository.add(type, title, message, relatedId)
}