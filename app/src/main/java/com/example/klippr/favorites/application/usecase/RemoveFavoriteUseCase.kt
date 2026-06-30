package com.example.klippr.favorites.application.usecase

import com.example.klippr.favorites.data.store.FavoriteStore

class RemoveFavoriteUseCase(private val repository: FavoriteStore){
    suspend operator fun invoke(favoriteId: String, userId: String) =
        repository.removeFavorite(favoriteId, userId)
}
