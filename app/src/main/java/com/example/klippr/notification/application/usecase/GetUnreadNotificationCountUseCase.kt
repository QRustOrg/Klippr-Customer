package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.data.store.NotificationStore
import kotlinx.coroutines.flow.Flow

class GetUnreadNotificationCountUseCase(private val repository: NotificationStore) {
    operator fun invoke(): Flow<Int> = repository.getUnreadCount()
}