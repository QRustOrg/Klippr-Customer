package com.example.klippr.favorites.data.repository

import com.example.klippr.favorites.data.mapper.toDomain
import com.example.klippr.favorites.data.remote.api.FavoriteApiService
import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl (
    private val api: FavoriteApiService,
) : FavoriteRepository {
    override suspend fun getFavoritesByUser(userId: String, archived: Boolean): List<Favorite> =
        api.getByUser(userId, archived).items.map { it.toDomain() }

    override suspend fun getFavoriteById(favoriteId: String): Favorite =
        api.getById(favoriteId).toDomain()

    override suspend fun saveFavorite(userId: String, promotionId: String): Favorite =
        api.save(mapOf("userId" to userId, "promotionId" to promotionId)).toDomain()

    override suspend fun removeFavorite(favoriteId: String, userId: String) =
        api.delete(favoriteId, userId)

    override suspend fun archiveFavorite(favoriteId: String, userId: String) =
        api.archive(favoriteId, userId)

    override suspend fun restoreFavorite(favoriteId: String, userId: String) =
        api.restore(favoriteId, userId)
}
