package com.example.klippr.favorites.data.repository

import com.example.klippr.favorites.data.remote.api.FavoriteApiService
import com.example.klippr.favorites.data.remote.dto.FavoriteDto
import com.example.klippr.favorites.data.remote.dto.FavoriteListDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FavoriteRepositoryImplTest {

    @Test
    fun getFavoriteById_mapsDtoToDomain() = runBlocking {
        val repository = FavoriteRepositoryImpl(FakeFavoriteApiService())

        val favorite = repository.getFavoriteById("favorite-1")

        assertEquals("favorite-1", favorite.favoriteId)
        assertEquals("user-1", favorite.userId)
        assertEquals("promo-1", favorite.promotionId)
        assertFalse(favorite.isArchived)
    }

    private class FakeFavoriteApiService : FavoriteApiService {
        override suspend fun getByUser(userId: String, archived: Boolean): FavoriteListDto =
            FavoriteListDto(userId = userId, count = 0, items = emptyList())

        override suspend fun getById(id: String): FavoriteDto =
            FavoriteDto(
                favoriteId = id,
                userId = "user-1",
                promotionId = "promo-1",
                isArchived = false,
                createdAt = null,
                updatedAt = null,
            )

        override suspend fun save(body: Map<String, String>): FavoriteDto =
            getById("favorite-created")

        override suspend fun delete(favoriteId: String, userId: String) = Unit
        override suspend fun archive(favoriteId: String, userId: String) = Unit
        override suspend fun restore(favoriteId: String, userId: String) = Unit
    }
}
