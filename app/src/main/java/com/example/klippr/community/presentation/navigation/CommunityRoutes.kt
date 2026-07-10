package com.example.klippr.community.presentation.navigation

/** Catalogo de rutas del bounded context Community. */
object CommunityRoutes {
    const val COMMUNITY = "community"
    const val COMMUNITY_WITH_PROMOTION = "community?promotionId={promotionId}"

    const val ARG_PROMOTION_ID = "promotionId"

    fun community(promotionId: String? = null) =
        if (promotionId != null) "community?promotionId=$promotionId" else "community"
}
