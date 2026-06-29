package com.example.klippr.redemption.application.usecase

import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.data.store.RedemptionStore

// @author Samuel Bonifacio
/** US-06: marca un código como canjeado vía el endpoint /confirm del backend. */
class ConfirmRedemptionUseCase(private val repository: RedemptionStore) {
    suspend operator fun invoke(code: RedemptionCode): RedemptionCode =
        repository.confirm(code)
}
