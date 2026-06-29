package com.example.klippr.favorites.application.usecase

import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.data.store.FavoriteStore

class GetUserFavoritesUseCase(private val repository: FavoriteStore) {
    suspend operator fun invoke(userId: String, archived: Boolean = false): List<Favorite> =
        repository.getFavoritesByUser(userId, archived)
}
