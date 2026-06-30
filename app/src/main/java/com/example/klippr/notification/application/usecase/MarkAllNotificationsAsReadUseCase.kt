package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.data.store.NotificationStore

class MarkAllNotificationsAsReadUseCase(private val repository: NotificationStore) {
    suspend operator fun invoke() = repository.markAllAsRead()
}