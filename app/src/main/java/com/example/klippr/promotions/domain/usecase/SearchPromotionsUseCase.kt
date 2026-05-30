package com.example.klippr.promotions.domain.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
class SearchPromotionsUseCase(private val repository: PromotionRepository) {
    operator fun invoke(query: String): Flow<List<Promotion>> = repository.search(query)
}
