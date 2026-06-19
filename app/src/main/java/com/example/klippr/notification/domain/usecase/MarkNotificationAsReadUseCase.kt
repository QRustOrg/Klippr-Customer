package com.example.klippr.notification.domain.usecase

import com.example.klippr.notification.domain.repository.NotificationRepository

class MarkNotificationAsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: String) = repository.markAsRead(id)
}