package com.example.klippr.favorites.domain.usecase

import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.domain.repository.FavoriteRepository

class GetFavoriteByIdUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(favoriteId: String): Favorite =
        repository.getFavoriteById(favoriteId)
}
