package com.example.klippr.redemption.domain.model

import com.example.klippr.promotions.domain.model.DiscountType

// @author Samuel Bonifacio
/**
 * Código de canje generado para una promoción (US-04/05/06).
 * Incluye un resumen denormalizado de la promo para pintar las tarjetas de "Mis Promos"
 * sin un segundo fetch (cuando el backend lo embebe; si no, el mapper lo enriquece).
 */
data class RedemptionCode(
    val id: String,
    val promotionId: String,
    val businessId: String?,
    val code: String,
    val token: String,
    val status: RedemptionStatus,
    val discountAppliedAmount: Double,
    val expiresAt: Long?,
    val redeemedAt: Long?,
    val blockedAt: Long?,
    // Resumen de la promo para la tarjeta
    val businessName: String?,
    val promotionTitle: String?,
    val discountValue: Double?,
    val discountType: DiscountType?,
    val imageKey: String?,
) {
    /** Contenido único que se codifica en el QR escaneable. */
    val qrContent: String get() = token.ifBlank { code }
}
