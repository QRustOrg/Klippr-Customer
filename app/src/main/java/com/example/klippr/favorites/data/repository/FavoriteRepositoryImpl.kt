package com.example.klippr.favorites.data.repository

import com.example.klippr.favorites.data.mapper.toDomain
import com.example.klippr.favorites.data.remote.api.FavoriteApiService
import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl (
    private val api: FavoriteApiService,
) : FavoriteRepository {
    override suspend fun getFavoritesByUser(userId: String): List<Favorite> =
        api.getByUser(userId).items.map { it.toDomain() }

    override suspend fun saveFavorite(userId: String, promotionId: String): Favorite =
        api.save(mapOf("userId" to userId, "promotionId" to promotionId)).toDomain()

    override suspend fun removeFavorite(favoriteId: String, userId: String) =
        api.delete(favoriteId, userId)
}
