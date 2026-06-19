package com.example.klippr.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.domain.usecase.AddNotificationUseCase
import com.example.klippr.notification.domain.usecase.GetNotificationsUseCase
import com.example.klippr.notification.domain.usecase.GetUnreadNotificationCountUseCase
import com.example.klippr.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.klippr.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.example.klippr.notification.presentation.state.NotificationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val getUnreadCount: GetUnreadNotificationCountUseCase,
    private val addNotification: AddNotificationUseCase,
    private val markAsRead: MarkNotificationAsReadUseCase,
    private val markAllAsRead: MarkAllNotificationsAsReadUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationUiState())
    val state: StateFlow<NotificationUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getNotifications()
                .combine(getUnreadCount()) { list, count -> list to count }
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { (list, count) ->
                    _state.update { it.copy(isLoading = false, notifications = list, unreadCount = count) }
                }
        }
    }

    fun notify(type: NotificationType, title: String, message: String, relatedId: String? = null) {
        viewModelScope.launch { addNotification(type, title, message, relatedId) }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch { markAsRead.invoke(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { markAllAsRead.invoke() }
    }
}