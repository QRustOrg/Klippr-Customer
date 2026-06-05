package com.example.klippr.navigation

// @author Samuel Bonifacio

/** Rutas de navegación de la app. Usar siempre estas constantes en lugar de strings literales. */
object Routes {
    const val SIGN_IN = "sign_in"
    const val EXPLORE = "explore"
    const val CREATE_PROMOTION = "create_promotion"
    const val MIS_PROMOS = "mis_promos"

    // Rutas con argumentos
    const val PROMOTION_DETAIL = "promotion_detail/{promotionId}"
    const val QR_CODE = "qr_code/{redemptionId}"

    fun promotionDetail(promotionId: String) = "promotion_detail/$promotionId"
    fun qrCode(redemptionId: String) = "qr_code/$redemptionId"

    const val ARG_PROMOTION_ID = "promotionId"
    const val ARG_REDEMPTION_ID = "redemptionId"
}
