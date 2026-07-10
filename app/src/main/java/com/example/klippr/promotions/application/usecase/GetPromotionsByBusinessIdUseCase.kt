package com.example.klippr.promotions.application.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.data.store.PromotionStore
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
class GetPromotionsByBusinessIdUseCase(private val repository: PromotionStore) {
    operator fun invoke(businessId: String): Flow<List<Promotion>> =
        repository.getByBusinessId(businessId)
}
