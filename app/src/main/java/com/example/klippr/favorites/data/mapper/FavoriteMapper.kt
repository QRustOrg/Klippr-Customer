package com.example.klippr.favorites.data.mapper

import com.example.klippr.favorites.data.remote.dto.FavoriteDto
import com.example.klippr.favorites.domain.model.Favorite

fun FavoriteDto.toDomain() = Favorite(
    favoriteId = favoriteId,
    userId = userId,
    promotionId = promotionId,
)