package com.example.klippr.redemption.domain.model

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionStatus

fun Promotion.redemptionBlockedMessage(nowMillis: Long = System.currentTimeMillis()): String? = when {
    status == PromotionStatus.CANCELLED -> "Esta promoción ya no está disponible."
    status == PromotionStatus.EXPIRED || endDate.toEpochMilli() < nowMillis -> "Esta promoción ya venció."
    startDate.toEpochMilli() > nowMillis -> "Esta promoción aún no está disponible."
    availableRedemptions != Int.MAX_VALUE && currentRedemptions >= availableRedemptions ->
        "Esta promoción ya no tiene canjes disponibles."
    else -> null
}
