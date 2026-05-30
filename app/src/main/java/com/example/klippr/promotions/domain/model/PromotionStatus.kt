package com.example.klippr.promotions.domain.model

// @author Samuel Bonifacio
/** Estados del ciclo de vida de una promoción. Persiste como String en Room. */
enum class PromotionStatus {
    DRAFT, PUBLISHED, EXPIRED, CANCELLED
}
