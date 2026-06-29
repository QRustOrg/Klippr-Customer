package com.example.klippr.promotions.application.usecase

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.data.store.PromotionStore
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
class SearchPromotionsUseCase(private val repository: PromotionStore) {
    operator fun invoke(query: String): Flow<List<Promotion>> = repository.search(query)
}
