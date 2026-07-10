package com.example.klippr.promotions.application.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.data.store.PromotionStore

// @author Samuel Bonifacio
class GetPromotionByIdUseCase(private val repository: PromotionStore) {
    suspend operator fun invoke(id: String): Promotion? = repository.getById(id)
}
