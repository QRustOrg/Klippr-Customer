package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.data.store.NotificationStore
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(private val repository: NotificationStore) {
    operator fun invoke(): Flow<List<Notification>> = repository.getAll()
}