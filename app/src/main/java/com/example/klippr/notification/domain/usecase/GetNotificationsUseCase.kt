package com.example.klippr.notification.domain.usecase

import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<List<Notification>> = repository.getAll()
}