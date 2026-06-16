package com.example.klippr.favorites.domain.model

data class Favorite(
    val favoriteId: String,
    val userId: String,
    val promotionId: String,
)