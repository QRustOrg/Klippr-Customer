package com.example.klippr.favorites.domain.repository

import com.example.klippr.favorites.domain.model.Favorite

interface FavoriteRepository{
    suspend fun getFavoritesByUser(userId: String): List<Favorite>
    suspend fun saveFavorite(userId: String, promotionId: String): Favorite
    suspend fun removeFavorite(favoriteId: String, userId: String)
}