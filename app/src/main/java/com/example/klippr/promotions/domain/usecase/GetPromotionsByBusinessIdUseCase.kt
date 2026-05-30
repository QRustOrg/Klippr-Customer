package com.example.klippr.promotions.domain.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
class GetPromotionsByBusinessIdUseCase(private val repository: PromotionRepository) {
    operator fun invoke(businessId: String): Flow<List<Promotion>> =
        repository.getByBusinessId(businessId)
}
