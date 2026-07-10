package com.example.klippr.promotions.application.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionStatus
import com.example.klippr.promotions.data.store.PromotionStore
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
class GetActivePromotionsUseCase(private val repository: PromotionStore) {
    operator fun invoke(): Flow<List<Promotion>> =
        repository.getByStatus(PromotionStatus.PUBLISHED)
}
