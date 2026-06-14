package com.example.klippr.promotions.domain.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
// Observa todas las promociones cacheadas (cualquier estado). El refresh remoto
// (GET /api/promotions) se dispara vía PromotionRepository.refreshAll().
class GetAllPromotionsUseCase(private val repository: PromotionRepository) {
    operator fun invoke(): Flow<List<Promotion>> = repository.getAll()
}
