package com.example.klippr.shared.presentation.navigation

/** Rutas del shell de la app (no pertenecen a un bounded context concreto). */
object MainRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val SETTINGS_DETAIL = "settings_detail/{section}"
    const val ARG_SETTINGS_SECTION = "section"

    fun settingsDetail(section: String) = "settings_detail/$section"
}
