package com.example.klippr.community.presentation.views

import com.example.klippr.favorites.domain.model.Favorite

internal fun favoriteByPromotionId(favorites: List<Favorite>): Map<String, Favorite> =
    favorites.associateBy { it.promotionId }

internal fun isRemoteImageModel(value: String?): Boolean =
    value?.startsWith("https://") == true || value?.startsWith("http://") == true
