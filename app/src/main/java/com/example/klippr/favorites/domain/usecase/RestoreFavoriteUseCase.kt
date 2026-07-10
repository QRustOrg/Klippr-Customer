package com.example.klippr.favorites.domain.usecase

import com.example.klippr.favorites.domain.repository.FavoriteRepository

class RestoreFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(favoriteId: String, userId: String) =
        repository.restoreFavorite(favoriteId, userId)
}
