package com.example.klippr.navigation

// @author Samuel Bonifacio

/** Rutas de navegación de la app. Usar siempre estas constantes en lugar de strings literales. */
object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val EXPLORE = "explore"
    const val MIS_PROMOS = "mis_promos"

    const val COMMUNITY = "community"

    // Rutas con argumentos
    const val PROMOTION_DETAIL = "promotion_detail/{promotionId}"
    const val QR_CODE = "qr_code/{redemptionId}"
    const val REDEMPTION_SUCCESS = "redemption_success/{redemptionId}"

    fun promotionDetail(promotionId: String) = "promotion_detail/$promotionId"
    fun qrCode(redemptionId: String) = "qr_code/$redemptionId"
    fun redemptionSuccess(redemptionId: String) = "redemption_success/$redemptionId"

    const val ARG_PROMOTION_ID = "promotionId"
    const val ARG_REDEMPTION_ID = "redemptionId"
}
