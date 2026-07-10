package com.example.klippr.notification.application.usecase

import com.example.klippr.notification.data.store.NotificationStore

class SyncNotificationsUseCase(private val repository: NotificationStore) {
    suspend operator fun invoke(userId: String) = repository.syncFromRemote(userId)
}
