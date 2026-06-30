package com.example.klippr.favorites.application.usecase

import com.example.klippr.favorites.data.store.FavoriteStore

class ArchiveFavoriteUseCase(private val repository: FavoriteStore) {
    suspend operator fun invoke(favoriteId: String, userId: String) =
        repository.archiveFavorite(favoriteId, userId)
}
