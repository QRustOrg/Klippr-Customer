package com.example.klippr.community.presentation.views

import com.example.klippr.community.domain.model.Review
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewReactionUiTest {

    @Test
    fun reviewReactionContent_showsInactiveStateAndCount() {
        val review = review(likeCount = 7, isLiked = false)

        val result = reviewReactionContent(review)

        assertEquals("7", result.label)
        assertEquals(false, result.selected)
    }

    @Test
    fun reviewReactionContent_showsActiveStateAndEmptyCountAsAction() {
        val review = review(likeCount = 0, isLiked = true)

        val result = reviewReactionContent(review)

        assertEquals("Reaccionaste", result.label)
        assertEquals(true, result.selected)
    }

    private fun review(likeCount: Int, isLiked: Boolean) = Review(
        id = "review-1",
        promotionId = "promo-1",
        promotionTitle = "Promo",
        promotionImageUrl = "",
        businessName = "Negocio",
        userId = "user-1",
        userName = "Usuario",
        userAvatarUrl = null,
        rating = 5,
        comment = "Buena promo",
        createdAt = 1_718_000_000_000,
        isVerifiedPurchase = true,
        likeCount = likeCount,
        isLikedByCurrentUser = isLiked,
    )
}
