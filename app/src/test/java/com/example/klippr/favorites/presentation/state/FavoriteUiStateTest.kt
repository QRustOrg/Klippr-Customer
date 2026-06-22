package com.example.klippr.favorites.presentation.state

import com.example.klippr.favorites.domain.model.Favorite
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteUiStateTest {

    @Test
    fun visibleAndArchivedFavoritesAreSeparatedByApiState() {
        val active = favorite("favorite-active", isArchived = false)
        val archived = favorite("favorite-archived", isArchived = true)

        val state = FavoriteUiState(
            favorites = listOf(active),
            archivedFavorites = listOf(archived),
        )

        assertEquals(listOf(active), state.visibleFavorites)
        assertEquals(listOf(archived), state.archivedFavorites)
    }

    private fun favorite(favoriteId: String, isArchived: Boolean) = Favorite(
        favoriteId = favoriteId,
        userId = "user-1",
        promotionId = "promo-$favoriteId",
        isArchived = isArchived,
    )
}
