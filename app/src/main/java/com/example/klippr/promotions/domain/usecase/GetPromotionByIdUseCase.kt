package com.example.klippr.promotions.domain.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.repository.PromotionRepository

// @author Samuel Bonifacio
class GetPromotionByIdUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke(id: String): Promotion? = repository.getById(id)
}
