package com.example.klippr.redemption.domain.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.repository.RedemptionRepository

// @author Samuel Bonifacio
/** US-04: genera el código QR único para una promoción. */
class GenerateRedemptionUseCase(private val repository: RedemptionRepository) {
    suspend operator fun invoke(consumerId: String, promotion: Promotion): RedemptionCode =
        repository.generate(consumerId, promotion)
}
