package com.example.klippr.favorites.data.mapper

import com.example.klippr.favorites.data.remote.dto.FavoriteDto
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteMapperTest {

    @Test
    fun toDomain_mapsArchiveState() {
        val archived = favoriteDto(isArchived = true).toDomain()
        val active = favoriteDto(isArchived = false).toDomain()

        assertTrue(archived.isArchived)
        assertFalse(active.isArchived)
    }

    private fun favoriteDto(isArchived: Boolean) = FavoriteDto(
        favoriteId = "favorite-1",
        userId = "user-1",
        promotionId = "promo-1",
        isArchived = isArchived,
        createdAt = null,
        updatedAt = null,
    )
}
