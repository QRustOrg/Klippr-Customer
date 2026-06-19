package com.example.klippr.notification.presentation.state

import com.example.klippr.notification.domain.model.Notification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null,
) {
    val isEmpty: Boolean get() = !isLoading && notifications.isEmpty() && error == null
}