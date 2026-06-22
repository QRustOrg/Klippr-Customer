package com.example.klippr.community.data.mapper

import com.example.klippr.community.data.remote.dto.ReviewDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewMapperTest {

    @Test
    fun toDomain_usesEmptyImageWhenPromotionImageIsNull() {
        val dto = ReviewDto(
            id = "review-1",
            promotionId = "promo-1",
            promotionTitle = "Hamburguesas promo 2x1",
            promotionImageUrl = null,
            businessName = "Klippr Burger",
            userId = "user-1",
            userName = "Samuel Bonifacio",
            userAvatarUrl = null,
            rating = 5,
            comment = "buena promocion",
            createdAt = 1_718_000_000_000,
            isVerifiedPurchase = true,
        )

        val result = dto.toDomain()

        assertEquals("", result.promotionImageUrl)
    }
}
