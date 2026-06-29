package com.example.klippr.promotions.application.usecase

import com.example.klippr.promotions.data.store.PromotionStore

// @author Samuel Bonifacio
class ToggleFavoriteUseCase(private val repository: PromotionStore) {
    suspend operator fun invoke(id: String, isFavorite: Boolean) =
        repository.toggleFavorite(id, isFavorite)
}
