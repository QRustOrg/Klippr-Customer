package com.example.klippr.redemption.data.mapper

import com.example.klippr.promotions.data.remote.api.PromotionApiService
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.redemption.data.remote.dto.RedemptionDto
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus
import java.time.Instant

// @author Samuel Bonifacio
/**
 * Convierte [RedemptionDto] a dominio. Si el backend no embebe el resumen de la promo,
 * lo enriquece con GET /api/promotions/{id} (fallback fetch, cacheado por petición).
 */
class RedemptionMapper(private val promotionApi: PromotionApiService) {

    // Caché por lote para no pedir la misma promo varias veces al mapear una lista.
    suspend fun toDomain(dto: RedemptionDto, promoCache: MutableMap<String, PromoSummary?> = mutableMapOf()): RedemptionCode {
        val promotionId = dto.promotionId.orEmpty()
        val expiresAt = dto.expiresAt.parseInstantOrNull()
        val redeemedAt = dto.confirmedAt.parseInstantOrNull()

        // Resumen embebido o, si falta, traído de la API de promociones.
        val embedded = PromoSummary(
            businessName = dto.businessName,
            title = dto.promotionTitle,
            discountValue = dto.discountValue,
            discountType = dto.discountType?.toDiscountType(),
            imageKey = null, // el backend no embebe imageKey en la redención
        )
        val summary = if (embedded.isComplete || promotionId.isBlank()) {
            embedded
        } else {
            promoCache.getOrPut(promotionId) { fetchSummary(promotionId) } ?: embedded
        }

        return RedemptionCode(
            id = dto.id ?: dto.code.orEmpty(),
            promotionId = promotionId,
            businessId = dto.businessId,
            code = dto.code.orEmpty(),
            token = dto.token.orEmpty(),
            status = resolveStatus(dto.status, expiresAt, redeemedAt),
            discountAppliedAmount = dto.discountAppliedAmount ?: summary.discountValue ?: 0.0,
            expiresAt = expiresAt,
            redeemedAt = redeemedAt,
            businessName = summary.businessName,
            promotionTitle = summary.title,
            discountValue = summary.discountValue,
            discountType = summary.discountType,
            imageKey = summary.imageKey,
        )
    }

    suspend fun toDomainList(dtos: List<RedemptionDto>): List<RedemptionCode> {
        val cache = mutableMapOf<String, PromoSummary?>()
        return dtos.map { toDomain(it, cache) }
    }

    private suspend fun fetchSummary(promotionId: String): PromoSummary? = try {
        val p = promotionApi.getById(promotionId)
        PromoSummary(
            businessName = p.businessId, // el backend de promo no trae nombre; se usa id como respaldo
            title = p.title,
            discountValue = p.discountAmount,
            discountType = p.discountType.toDiscountType(),
            imageKey = p.imageKey,
        )
    } catch (e: Exception) {
        null
    }

    data class PromoSummary(
        val businessName: String?,
        val title: String?,
        val discountValue: Double?,
        val discountType: DiscountType?,
        val imageKey: String?,
    ) {
        val isComplete: Boolean get() = title != null && discountValue != null && discountType != null
    }
}

// Normaliza el status del backend; deriva EXPIRED si la fecha de vencimiento ya pasó.
private fun resolveStatus(raw: String?, expiresAt: Instant?, redeemedAt: Instant?): RedemptionStatus {
    val normalized = raw?.trim()?.replace(Regex("[_\\-\\s]"), "")?.lowercase()
    val base = when (normalized) {
        "redeemed", "confirmed", "used", "completed" -> RedemptionStatus.REDEEMED
        "expired", "cancelled", "canceled", "void"   -> RedemptionStatus.EXPIRED
        "active", "pending", "created", "issued", "generated", null, "" -> RedemptionStatus.ACTIVE
        else -> RedemptionStatus.ACTIVE
    }
    if (base == RedemptionStatus.REDEEMED || redeemedAt != null) return RedemptionStatus.REDEEMED
    if (base == RedemptionStatus.ACTIVE && expiresAt != null && expiresAt.isBefore(Instant.now())) {
        return RedemptionStatus.EXPIRED
    }
    return base
}

private fun String?.parseInstantOrNull(): Instant? =
    this?.takeIf { it.isNotBlank() }?.let { runCatching { Instant.parse(it) }.getOrNull() }

private fun String.toDiscountType(): DiscountType = when (
    trim().replace(Regex("[_\\-\\s]"), "").lowercase()
) {
    "fixedamount", "fixed", "amount" -> DiscountType.FIXED_AMOUNT
    else -> DiscountType.PERCENTAGE
}
