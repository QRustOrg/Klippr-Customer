package com.example.klippr.promotions.application.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.data.store.PromotionStore
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
// Observa todas las promociones cacheadas (cualquier estado). El refresh remoto
// (GET /api/promotions) se dispara vía PromotionStore.refreshAll().
class GetAllPromotionsUseCase(private val repository: PromotionStore) {
    operator fun invoke(): Flow<List<Promotion>> = repository.getAll()
}
