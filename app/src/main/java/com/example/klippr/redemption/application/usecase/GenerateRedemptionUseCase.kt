package com.example.klippr.redemption.application.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.data.store.RedemptionStore

// @author Samuel Bonifacio
/** US-04: genera el código QR único para una promoción. */
class GenerateRedemptionUseCase(private val repository: RedemptionStore) {
    suspend operator fun invoke(consumerId: String, promotion: Promotion): RedemptionCode =
        repository.generate(consumerId, promotion)
}
