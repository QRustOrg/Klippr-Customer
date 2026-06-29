package com.example.klippr.redemption.presentation.navigation

/** Catalogo de rutas del bounded context Redemption. */
object RedemptionRoutes {
    const val MIS_PROMOS = "mis_promos"
    const val MIS_PROMOS_WITH_TAB = "mis_promos?tab={tab}"
    const val QR_CODE = "qr_code/{redemptionId}"
    const val REDEMPTION_SUCCESS = "redemption_success/{redemptionId}"

    const val ARG_REDEMPTION_ID = "redemptionId"
    const val ARG_TAB = "tab"
    const val TAB_FAVORITES = "favorites"
    const val TAB_ARCHIVED = "archived"
    const val TAB_CODES = "codes"

    fun qrCode(redemptionId: String) = "qr_code/$redemptionId"
    fun redemptionSuccess(redemptionId: String) = "redemption_success/$redemptionId"
    fun misPromos(tab: String) = "mis_promos?tab=$tab"
}
