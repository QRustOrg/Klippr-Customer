package com.example.klippr.promotions.presentation.navigation

/** Catalogo de rutas del bounded context Promotions. */
object PromotionRoutes {
    const val EXPLORE = "explore"
    const val PROMOTION_DETAIL = "promotion_detail/{promotionId}"

    const val ARG_PROMOTION_ID = "promotionId"

    fun promotionDetail(promotionId: String) = "promotion_detail/$promotionId"
}
