package com.example.klippr.redemption.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.iam.application.usecase.GetCurrentUserUseCase
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.application.usecase.ConfirmRedemptionUseCase
import com.example.klippr.redemption.application.usecase.GenerateRedemptionUseCase
import com.example.klippr.redemption.application.usecase.GetConsumerRedemptionsUseCase
import com.example.klippr.redemption.application.usecase.GetRedemptionByIdUseCase
import com.example.klippr.redemption.presentation.state.RedemptionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.klippr.shared.core.ServiceLocator

// @author Samuel Bonifacio
/** ViewModel de Redemption: genera códigos (US-04) y carga el historial (US-05/06). */
class RedemptionViewModel(
    private val generateRedemption: GenerateRedemptionUseCase,
    private val getConsumerRedemptions: GetConsumerRedemptionsUseCase,
    private val getRedemptionById: GetRedemptionByIdUseCase,
    private val confirmRedemption: ConfirmRedemptionUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RedemptionUiState())
    val state: StateFlow<RedemptionUiState> = _state.asStateFlow()

    /** Carga todos los códigos del consumidor autenticado (las 3 pestañas). */
    fun loadHistory() {
        viewModelScope.launch {
            val consumerId = getCurrentUser()?.userId
            if (consumerId == null) {
                _state.update { it.copy(isLoading = false, error = "Sesión no encontrada") }
                return@launch
            }
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val codes = getConsumerRedemptions(consumerId)
                _state.update { it.copy(isLoading = false, codes = codes) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar códigos") }
            }
        }
    }

    /** US-04: genera el código de [promotion]; deja el resultado en `generated`. */
    fun generate(promotion: Promotion) {
        viewModelScope.launch {
            val consumerId = getCurrentUser()?.userId
            if (consumerId == null) {
                _state.update { it.copy(error = "Sesión no encontrada") }
                return@launch
            }
            _state.update { it.copy(isGenerating = true, error = null, generated = null) }
            try {
                val code = generateRedemption(consumerId, promotion)
                _state.update {
                    it.copy(
                        isGenerating = false,
                        generated = code,
                        selectedCode = code,
                        codeError = null,
                        codes = listOf(code) + it.codes.filterNot { c -> c.id == code.id },
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isGenerating = false, error = e.message ?: "Error al generar código") }
            }
        }
    }

    /** US-06: marca [code] como canjeado y recarga el historial al confirmar. */
    fun markRedeemed(code: RedemptionCode) {
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, error = null) }
            try {
                confirmRedemption(code)
                _state.update { it.copy(isGenerating = false) }
                loadHistory()
            } catch (e: Exception) {
                _state.update { it.copy(isGenerating = false, error = e.message ?: "Error al confirmar canje") }
            }
        }
    }

    fun consumeGenerated() = _state.update { it.copy(generated = null) }

    fun consumeError() = _state.update { it.copy(error = null, codeError = null) }

    /** Carga un codigo por id para abrir QR desde ruta directa o tras perder estado en memoria. */
    fun loadCodeById(id: String) {
        if (id.isBlank()) {
            _state.update { it.copy(isLoadingCode = false, selectedCode = null, codeError = "Codigo no encontrado") }
            return
        }

        _state.value.codeById(id)?.let { cached ->
            _state.update { it.copy(isLoadingCode = false, selectedCode = cached, codeError = null) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoadingCode = true, selectedCode = null, codeError = null) }
            try {
                val code = getRedemptionById(id)
                _state.update {
                    it.copy(
                        isLoadingCode = false,
                        selectedCode = code,
                        codeError = null,
                        codes = listOf(code) + it.codes.filterNot { existing -> existing.id == code.id },
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCode = false,
                        codeError = e.message ?: "Error al cargar codigo",
                    )
                }
            }
        }
    }

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = RedemptionViewModel(
                    generateRedemption = GenerateRedemptionUseCase(serviceLocator.redemptionStore),
                    getConsumerRedemptions = GetConsumerRedemptionsUseCase(serviceLocator.redemptionStore),
                    getRedemptionById = GetRedemptionByIdUseCase(serviceLocator.redemptionStore),
                    confirmRedemption = ConfirmRedemptionUseCase(serviceLocator.redemptionStore),
                    getCurrentUser = GetCurrentUserUseCase(serviceLocator.authStore),
                ) as T
            }
    }

}