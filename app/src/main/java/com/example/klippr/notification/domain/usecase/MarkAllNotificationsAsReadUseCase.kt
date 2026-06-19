package com.example.klippr.notification.domain.usecase

import com.example.klippr.notification.domain.repository.NotificationRepository

class MarkAllNotificationsAsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.markAllAsRead()
}