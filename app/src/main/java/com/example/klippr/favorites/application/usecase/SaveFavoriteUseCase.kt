package com.example.klippr.favorites.application.usecase

import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.data.store.FavoriteStore

class SaveFavoriteUseCase(private val repository: FavoriteStore){
    suspend operator fun invoke(userId: String, promotionId: String): Favorite =
        repository.saveFavorite(userId, promotionId)
}
