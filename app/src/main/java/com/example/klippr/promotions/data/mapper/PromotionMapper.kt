package com.example.klippr.promotions.data.mapper

import com.example.klippr.promotions.data.local.entity.PromotionEntity
import com.example.klippr.promotions.data.remote.dto.PromotionDto
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import java.time.Instant

// @author Samuel Bonifacio
// isFavorite se recibe como parámetro para preservar el flag local al hacer refresh desde la red.
fun PromotionDto.toEntity(isFavorite: Boolean = false): PromotionEntity = PromotionEntity(
    id                   = id,
    businessId           = businessId,
    title                = title,
    description          = description,
    discountValue        = discountAmount,
    discountType         = discountType.toKotlinDiscountType().name,
    status               = status.toKotlinPromotionStatus().name,
    imageUrl             = null,
    termsAndConditions   = null,
    availableRedemptions = redemptionCap ?: Int.MAX_VALUE,
    currentRedemptions   = 0,
    startDate            = Instant.parse(startDate),
    endDate              = Instant.parse(endDate),
    createdAt            = Instant.parse(createdAt),
    updatedAt            = updatedAt.takeIf { it.isNotBlank() }?.let { Instant.parse(it) },
    isFavorite           = isFavorite,
    category             = PromotionCategory.OTHER.name,
    locationName         = null,
    businessName         = null,
    rating               = null,
)

fun PromotionEntity.toDomain(): Promotion = Promotion(
    id                   = id,
    businessId           = businessId,
    title                = title,
    description          = description,
    discountValue        = discountValue,
    discountType         = DiscountType.valueOf(discountType),
    status               = PromotionStatus.valueOf(status),
    imageUrl             = imageUrl,
    termsAndConditions   = termsAndConditions,
    availableRedemptions = availableRedemptions,
    currentRedemptions   = currentRedemptions,
    startDate            = startDate,
    endDate              = endDate,
    createdAt            = createdAt,
    updatedAt            = updatedAt,
    isFavorite           = isFavorite,
    category             = PromotionCategory.valueOf(category),
    locationName         = locationName,
    businessName         = businessName,
    rating               = rating,
)

fun Promotion.toEntity(): PromotionEntity = PromotionEntity(
    id                   = id,
    businessId           = businessId,
    title                = title,
    description          = description,
    discountValue        = discountValue,
    discountType         = discountType.name,
    status               = status.name,
    imageUrl             = imageUrl,
    termsAndConditions   = termsAndConditions,
    availableRedemptions = availableRedemptions,
    currentRedemptions   = currentRedemptions,
    startDate            = startDate,
    endDate              = endDate,
    createdAt            = createdAt,
    updatedAt            = updatedAt,
    isFavorite           = isFavorite,
    category             = category.name,
    locationName         = locationName,
    businessName         = businessName,
    rating               = rating,
)

// Normaliza PascalCase/variantes del backend ("Percentage", "Percent", etc.) al enum Kotlin.
private fun String.toKotlinDiscountType(): DiscountType = when (
    trim().replace(Regex("[_\\-\\s]"), "").lowercase()
) {
    "percentage", "percent"           -> DiscountType.PERCENTAGE
    "fixedamount", "fixed", "amount"  -> DiscountType.FIXED_AMOUNT
    else                              -> DiscountType.PERCENTAGE
}

// Normaliza status PascalCase del backend ("Draft", "Published", etc.) al enum Kotlin.
private fun String.toKotlinPromotionStatus(): PromotionStatus = when (trim().lowercase()) {
    "draft"     -> PromotionStatus.DRAFT
    "published" -> PromotionStatus.PUBLISHED
    "expired"   -> PromotionStatus.EXPIRED
    "cancelled" -> PromotionStatus.CANCELLED
    else        -> PromotionStatus.DRAFT
}
