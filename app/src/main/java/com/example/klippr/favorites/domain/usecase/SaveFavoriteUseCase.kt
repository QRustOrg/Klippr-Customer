package com.example.klippr.favorites.domain.usecase

import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.domain.repository.FavoriteRepository

class SaveFavoriteUseCase(private val repository: FavoriteRepository){
    suspend operator fun invoke(userId: String, promotionId: String): Favorite =
        repository.saveFavorite(userId, promotionId)
}
