package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.data.store.NotificationStore
import com.example.klippr.notification.domain.model.NotificationType

class AddNotificationUseCase(private val repository: NotificationStore) {
    suspend operator fun invoke(
        userId: String?,
        type: NotificationType,
        title: String,
        message: String,
        relatedId: String? = null,
    ) = repository.add(userId, type, title, message, relatedId)
}
