package com.example.klippr.promotions.presentation.view

import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class PromotionDetailScreenTest {

    @Test
    fun redemptionsLabel_usesRemainingRedemptionsFromBackendCounts() {
        val promotion = promotion(availableRedemptions = 5, currentRedemptions = 3)

        assertEquals("2 canjes disponibles", redemptionsLabel(promotion))
    }

    @Test
    fun redemptionsLabel_usesSingularForOneRemainingRedemption() {
        val promotion = promotion(availableRedemptions = 5, currentRedemptions = 4)

        assertEquals("1 canje disponible", redemptionsLabel(promotion))
    }

    @Test
    fun redemptionsLabel_showsSoldOutWhenNoRemainingRedemptions() {
        val promotion = promotion(availableRedemptions = 5, currentRedemptions = 5)

        assertEquals("Sin canjes disponibles", redemptionsLabel(promotion))
    }

    @Test
    fun redemptionsLabel_showsUnlimitedWhenBackendHasNoCap() {
        val promotion = promotion(availableRedemptions = Int.MAX_VALUE, currentRedemptions = 20)

        assertEquals("Canjes ilimitados disponibles", redemptionsLabel(promotion))
    }
}

private fun promotion(
    availableRedemptions: Int,
    currentRedemptions: Int,
) = Promotion(
    id = "promo-1",
    businessId = "business-1",
    title = "Promo",
    description = "Promo test",
    discountValue = 10.0,
    discountType = DiscountType.PERCENTAGE,
    status = PromotionStatus.PUBLISHED,
    imageUrl = null,
    imageKey = null,
    termsAndConditions = null,
    availableRedemptions = availableRedemptions,
    currentRedemptions = currentRedemptions,
    startDate = Instant.EPOCH,
    endDate = Instant.parse("2026-08-13T00:00:00Z"),
    createdAt = Instant.EPOCH,
    updatedAt = null,
    isFavorite = false,
    category = PromotionCategory.FOOD,
    locationName = null,
    businessName = "pizza hot",
    rating = null,
)
