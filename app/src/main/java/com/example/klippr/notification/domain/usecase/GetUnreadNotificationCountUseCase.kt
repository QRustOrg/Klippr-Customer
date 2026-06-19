package com.example.klippr.notification.domain.usecase

import com.example.klippr.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetUnreadNotificationCountUseCase(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<Int> = repository.getUnreadCount()
}