package com.example.klippr.favorites.application.usecase

import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.data.store.FavoriteStore

class GetFavoriteByIdUseCase(private val repository: FavoriteStore) {
    suspend operator fun invoke(favoriteId: String): Favorite =
        repository.getFavoriteById(favoriteId)
}
