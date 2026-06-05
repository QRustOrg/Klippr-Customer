package com.example.klippr.redemption.domain.usecase

import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.repository.RedemptionRepository

// @author Samuel Bonifacio
/** US-05/06: obtiene todos los códigos del consumidor para alimentar las pestañas. */
class GetConsumerRedemptionsUseCase(private val repository: RedemptionRepository) {
    suspend operator fun invoke(consumerId: String): List<RedemptionCode> =
        repository.getByConsumer(consumerId)
}
