package com.example.klippr.redemption.presentation.state

import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus

// @author Samuel Bonifacio
/** Estado de "Mis Promos" + resultado de generación (US-04/05/06). */
data class RedemptionUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val isLoadingCode: Boolean = false,
    val codes: List<RedemptionCode> = emptyList(),
    val generated: RedemptionCode? = null,
    val selectedCode: RedemptionCode? = null,
    val error: String? = null,
    val codeError: String? = null,
) {
    val active: List<RedemptionCode> get() = codes.filter { it.status == RedemptionStatus.ACTIVE }
    val redeemed: List<RedemptionCode> get() = codes.filter { it.status == RedemptionStatus.REDEEMED }
    val expired: List<RedemptionCode> get() = codes.filter { it.status == RedemptionStatus.EXPIRED }

    fun codeById(id: String): RedemptionCode? =
        generated?.takeIf { it.id == id }
            ?: selectedCode?.takeIf { it.id == id }
            ?: codes.firstOrNull { it.id == id }
}
