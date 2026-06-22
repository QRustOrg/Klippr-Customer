package com.example.klippr.community.presentation.view

import com.example.klippr.favorites.domain.model.Favorite
import org.junit.Assert.assertEquals
import org.junit.Test

class CommunityFavoriteMapperTest {

    @Test
    fun favoriteByPromotionId_usesPromotionIdAsLookupKey() {
        val pizzaFavorite = Favorite(
            favoriteId = "favorite-1",
            userId = "user-1",
            promotionId = "promo-pizza",
        )
        val burgerFavorite = Favorite(
            favoriteId = "favorite-2",
            userId = "user-1",
            promotionId = "promo-burger",
        )

        val result = favoriteByPromotionId(listOf(pizzaFavorite, burgerFavorite))

        assertEquals(pizzaFavorite, result["promo-pizza"])
        assertEquals(burgerFavorite, result["promo-burger"])
    }

    @Test
    fun isRemoteImageModel_acceptsOnlyHttpUrls() {
        assertEquals(true, isRemoteImageModel("https://example.com/promo.png"))
        assertEquals(true, isRemoteImageModel("http://example.com/promo.png"))
        assertEquals(false, isRemoteImageModel("comida_hamburguesas"))
        assertEquals(false, isRemoteImageModel(""))
        assertEquals(false, isRemoteImageModel(null))
    }
}
