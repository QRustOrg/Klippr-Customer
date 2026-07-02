package com.example.klippr.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.klippr.iam.data.store.AuthStore
import com.example.klippr.profile.data.store.ProfileStore
import com.example.klippr.profile.presentation.state.ProfileStats
import com.example.klippr.profile.presentation.state.ProfileUiState
import com.example.klippr.redemption.data.store.RedemptionStore
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus
import com.example.klippr.shared.core.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @author Samuel Bonifacio
/** Carga perfil, preferencias y resumen de canjes para "Mi perfil". */
class ProfileViewModel(
    private val profileStore: ProfileStore,
    private val redemptionStore: RedemptionStore,
    private val authStore: AuthStore,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, activityError = null) }
            try {
                val profile = profileStore.getCurrentProfile()
                val userId = authStore.currentUser()?.userId ?: profile.userId
                val redemptionsResult = runCatching { redemptionStore.getByConsumer(userId) }
                val redemptions = redemptionsResult.getOrElse { emptyList() }
                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        stats = redemptions.toStats(),
                        latestRedemptions = redemptions.take(3),
                        activityError = redemptionsResult.exceptionOrNull()?.message,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "No se pudo cargar el perfil")
                }
            }
        }
    }

    fun markProfileSaveUnavailable() {
        _state.update {
            it.copy(profileSaveMessage = "La edicion de datos personales requiere que el backend entregue profileId.")
        }
    }

    fun consumeProfileSaveMessage() {
        _state.update { it.copy(profileSaveMessage = null) }
    }

    private fun List<RedemptionCode>.toStats() = ProfileStats(
        totalRedemptions = size,
        activeRedemptions = count { it.status == RedemptionStatus.ACTIVE },
        redeemedRedemptions = count { it.status == RedemptionStatus.REDEEMED },
        totalSavings = map { it.discountAppliedAmount }.takeIf { it.isNotEmpty() }?.sum(),
        averageTransactionValue = map { it.discountAppliedAmount }.takeIf { it.isNotEmpty() }?.average(),
    )

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(
                    profileStore = serviceLocator.profileStore,
                    redemptionStore = serviceLocator.redemptionStore,
                    authStore = serviceLocator.authStore,
                ) as T
            }
    }
}
