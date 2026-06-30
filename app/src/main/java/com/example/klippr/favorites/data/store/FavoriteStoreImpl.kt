package com.example.klippr.favorites.data.store

import com.example.klippr.favorites.data.network.FavoriteWebService
import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.domain.model.FavoriteResource

/**
 * Implementacion de [FavoriteStore] sobre la API de Favorites.
 * El mapeo recurso de cable -> modelo de dominio se hace en linea (sin capa mapper aparte).
 */
class FavoriteStoreImpl(
    private val webService: FavoriteWebService,
) : FavoriteStore {

    override suspend fun getFavoritesByUser(userId: String, archived: Boolean): List<Favorite> =
        webService.getByUser(userId, archived).items.map { it.toDomain() }

    override suspend fun getFavoriteById(favoriteId: String): Favorite =
        webService.getById(favoriteId).toDomain()

    override suspend fun saveFavorite(userId: String, promotionId: String): Favorite =
        webService.save(mapOf("userId" to userId, "promotionId" to promotionId)).toDomain()

    override suspend fun removeFavorite(favoriteId: String, userId: String) =
        webService.delete(favoriteId, userId)

    override suspend fun archiveFavorite(favoriteId: String, userId: String) =
        webService.archive(favoriteId, userId)

    override suspend fun restoreFavorite(favoriteId: String, userId: String) =
        webService.restore(favoriteId, userId)

    private fun FavoriteResource.toDomain() = Favorite(
        favoriteId = favoriteId,
        userId = userId,
        promotionId = promotionId,
        isArchived = isArchived,
    )
}
