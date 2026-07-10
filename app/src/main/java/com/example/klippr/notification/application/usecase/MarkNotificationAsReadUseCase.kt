package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.data.store.NotificationStore

class MarkNotificationAsReadUseCase(private val repository: NotificationStore) {
    suspend operator fun invoke(id: String) = repository.markAsRead(id)
}