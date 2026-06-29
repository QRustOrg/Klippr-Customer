package com.example.klippr.favorites.domain.model

import com.google.gson.annotations.SerializedName

data class FavoriteResource(
    @SerializedName("favoriteId")  val favoriteId: String,
    @SerializedName("userId")      val userId: String,
    @SerializedName("promotionId") val promotionId: String,
    @SerializedName("isArchived")  val isArchived: Boolean = false,
    @SerializedName("createdAt")   val createdAt: String?,
    @SerializedName("updatedAt")   val updatedAt: String?,
)

data class FavoriteListResource(
    @SerializedName("userId") val userId: String,
    @SerializedName("count")  val count: Int,
    @SerializedName("items")  val items: List<FavoriteResource>,
)
