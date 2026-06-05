package com.example.klippr.redemption.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.iam.domain.usecase.GetCurrentUserUseCase
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.domain.usecase.GenerateRedemptionUseCase
import com.example.klippr.redemption.domain.usecase.GetConsumerRedemptionsUseCase
import com.example.klippr.redemption.presentation.state.RedemptionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @author Samuel Bonifacio
/** ViewModel de Redemption: genera códigos (US-04) y carga el historial (US-05/06). */
class RedemptionViewModel(
    private val generateRedemption: GenerateRedemptionUseCase,
    private val getConsumerRedemptions: GetConsumerRedemptionsUseCase,
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
                        codes = listOf(code) + it.codes.filterNot { c -> c.id == code.id },
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isGenerating = false, error = e.message ?: "Error al generar código") }
            }
        }
    }

    fun consumeGenerated() = _state.update { it.copy(generated = null) }

    fun consumeError() = _state.update { it.copy(error = null) }
}
