package com.example.klippr.promotions.data.mapper

import com.example.klippr.promotions.data.local.entity.PromotionEntity
import com.example.klippr.promotions.data.remote.dto.PromotionDto
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

// @author Samuel Bonifacio
// Mappers para convertir entre PromotionDto (API), PromotionEntity (caché local) y Promotion (dominio).
fun PromotionDto.toEntity(isFavorite: Boolean = false): PromotionEntity = PromotionEntity(
    id                   = id,
    businessId           = businessId,
    title                = title,
    description          = description,
    discountValue        = discountAmount,
    discountType         = discountType.toKotlinDiscountType().name,
    status               = status.toKotlinPromotionStatus().name,
    imageUrl             = null,
    imageKey             = imageKey,
    termsAndConditions   = null,
    availableRedemptions = redemptionCap ?: Int.MAX_VALUE,
    currentRedemptions   = 0,
    startDate            = startDate.toInstantFlexible(),
    endDate              = endDate.toInstantFlexible(),
    createdAt            = createdAt.toInstantFlexible(),
    updatedAt            = updatedAt.takeIf { it.isNotBlank() }?.toInstantFlexible(),
    isFavorite           = isFavorite,
    category             = PromotionCategory.OTHER.name,
    locationName         = null,
    businessName         = null,
    rating               = null,
)

// El backend envía timestamps sin zona horaria ("2026-06-15T14:54:57.654618"), que Instant.parse
// rechaza por falta de offset. Intenta Instant primero y cae a LocalDateTime asumiendo UTC.
private fun String.toInstantFlexible(): Instant =
    runCatching { Instant.parse(this) }
        .getOrElse { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC) }

// Convierte PromotionEntity a Promotion, mapeando campos y normalizando enums.
fun PromotionEntity.toDomain(): Promotion = Promotion(
    id                   = id,
    businessId           = businessId,
    title                = title,
    description          = description,
    discountValue        = discountValue,
    discountType         = DiscountType.valueOf(discountType),
    status               = PromotionStatus.valueOf(status),
    imageUrl             = imageUrl,
    imageKey             = imageKey,
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

// Convierte Promotion a PromotionEntity, mapeando campos y normalizando enums.
fun Promotion.toEntity(): PromotionEntity = PromotionEntity(
    id                   = id,
    businessId           = businessId,
    title                = title,
    description          = description,
    discountValue        = discountValue,
    discountType         = discountType.name,
    status               = status.name,
    imageUrl             = imageUrl,
    imageKey             = imageKey,
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
