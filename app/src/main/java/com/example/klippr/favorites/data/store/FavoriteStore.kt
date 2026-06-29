package com.example.klippr.favorites.data.store

import com.example.klippr.favorites.domain.model.Favorite

interface FavoriteStore{
    suspend fun getFavoritesByUser(userId: String, archived: Boolean = false): List<Favorite>
    suspend fun getFavoriteById(favoriteId: String): Favorite
    suspend fun saveFavorite(userId: String, promotionId: String): Favorite
    suspend fun removeFavorite(favoriteId: String, userId: String)
    suspend fun archiveFavorite(favoriteId: String, userId: String)
    suspend fun restoreFavorite(favoriteId: String, userId: String)
}
