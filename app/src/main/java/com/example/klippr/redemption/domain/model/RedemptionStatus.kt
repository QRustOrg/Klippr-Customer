package com.example.klippr.redemption.domain.model

// @author Samuel Bonifacio
/** Estado de un código de canje, alineado con las pestañas de "Mis Promos". */
enum class RedemptionStatus {
    /** Generado y vigente → pestaña Activos (US-05). */
    ACTIVE,

    /** Ya canjeado/usado → pestaña Canjeados (US-06, historial). */
    REDEEMED,

    /** Vencido sin usar → pestaña Expirados. */
    EXPIRED,
}
