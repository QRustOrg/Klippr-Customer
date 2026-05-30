package com.example.klippr.promotions.domain.model

import java.time.Instant

// @author Samuel Bonifacio
/** Modelo de dominio puro de una promoción. Sin anotaciones de infraestructura. */
data class Promotion(
    val id: String,
    val businessId: String,
    val title: String,
    val description: String,
    val discountValue: Double,
    val discountType: DiscountType,
    val status: PromotionStatus,
    val imageUrl: String?,
    val termsAndConditions: String?,
    val availableRedemptions: Int,
    val currentRedemptions: Int,
    val startDate: Instant,
    val endDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val isFavorite: Boolean,
    val category: PromotionCategory,
    val locationName: String?,
    val businessName: String?,
    val rating: Double?,
)
