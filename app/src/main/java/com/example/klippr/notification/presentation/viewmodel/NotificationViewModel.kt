package com.example.klippr.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.klippr.notification.application.usecase.AddNotificationUseCase
import com.example.klippr.notification.application.usecase.GetNotificationsUseCase
import com.example.klippr.notification.application.usecase.GetUnreadNotificationCountUseCase
import com.example.klippr.notification.application.usecase.MarkAllNotificationsAsReadUseCase
import com.example.klippr.notification.application.usecase.MarkNotificationAsReadUseCase
import com.example.klippr.notification.application.usecase.SyncNotificationsUseCase
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.presentation.state.NotificationUiState
import com.example.klippr.shared.core.ServiceLocator
import com.example.klippr.shared.data.store.SessionDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val getUnreadCount: GetUnreadNotificationCountUseCase,
    private val addNotification: AddNotificationUseCase,
    private val markAsRead: MarkNotificationAsReadUseCase,
    private val markAllAsRead: MarkAllNotificationsAsReadUseCase,
    private val syncNotifications: SyncNotificationsUseCase,
    private val sessionStore: SessionDataStore,
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
                    _state.update {
                        it.copy(isLoading = false, notifications = list, unreadCount = count)
                    }
                }
        }
        // Primera sync al arrancar si hay sesión.
        refreshFromRemote()
    }

    fun refreshFromRemote() {
        viewModelScope.launch {
            val userId = sessionStore.session.first()?.user?.userId ?: return@launch
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { syncNotifications(userId) }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun notify(type: NotificationType, title: String, message: String, relatedId: String? = null) {
        viewModelScope.launch {
            val userId = sessionStore.session.first()?.user?.userId
            addNotification(userId, type, title, message, relatedId)
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch { markAsRead.invoke(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = sessionStore.session.first()?.user?.userId
            markAllAsRead.invoke(userId)
        }
    }

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = NotificationViewModel(
                    getNotifications = GetNotificationsUseCase(serviceLocator.notificationStore),
                    getUnreadCount = GetUnreadNotificationCountUseCase(serviceLocator.notificationStore),
                    addNotification = AddNotificationUseCase(serviceLocator.notificationStore),
                    markAsRead = MarkNotificationAsReadUseCase(serviceLocator.notificationStore),
                    markAllAsRead = MarkAllNotificationsAsReadUseCase(serviceLocator.notificationStore),
                    syncNotifications = SyncNotificationsUseCase(serviceLocator.notificationStore),
                    sessionStore = serviceLocator.sessionStore,
                ) as T
            }
    }
}
