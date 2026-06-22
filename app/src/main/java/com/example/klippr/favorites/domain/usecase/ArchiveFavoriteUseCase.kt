package com.example.klippr.favorites.domain.usecase

import com.example.klippr.favorites.domain.repository.FavoriteRepository

class ArchiveFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(favoriteId: String, userId: String) =
        repository.archiveFavorite(favoriteId, userId)
}
