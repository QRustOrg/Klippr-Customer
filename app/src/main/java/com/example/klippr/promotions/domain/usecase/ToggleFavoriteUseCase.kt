package com.example.klippr.promotions.domain.usecase

import com.example.klippr.promotions.domain.repository.PromotionRepository

// @author Samuel Bonifacio
class ToggleFavoriteUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke(id: String, isFavorite: Boolean) =
        repository.toggleFavorite(id, isFavorite)
}
