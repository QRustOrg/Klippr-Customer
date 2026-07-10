package com.example.klippr.notification.data.mapper

import com.example.klippr.notification.domain.model.NotificationType

/** Mapeo entre enum local y valores del backend. */
object NotificationTypeMapper {

    fun toApi(type: NotificationType): String = when (type) {
        NotificationType.REDEMPTION_GENERATED -> "RedemptionGenerated"
        NotificationType.REDEMPTION_EXPIRING -> "RedemptionExpiring"
        NotificationType.FAVORITE_ADDED -> "FavoriteAdded"
    }

    fun fromApi(raw: String?): NotificationType {
        return when (raw?.trim()) {
            "RedemptionGenerated", "REDEMPTION_GENERATED" -> NotificationType.REDEMPTION_GENERATED
            "RedemptionExpiring", "REDEMPTION_EXPIRING" -> NotificationType.REDEMPTION_EXPIRING
            "FavoriteAdded", "FAVORITE_ADDED" -> NotificationType.FAVORITE_ADDED
            else -> NotificationType.FAVORITE_ADDED // fallback seguro para filas raras
        }
    }
}
