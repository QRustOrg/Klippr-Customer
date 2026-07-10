package com.example.klippr.favorites.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FavoriteDto(
    @SerializedName("favoriteId")  val favoriteId: String,
    @SerializedName("userId")      val userId: String,
    @SerializedName("promotionId") val promotionId: String,
    @SerializedName("isArchived")  val isArchived: Boolean = false,
    @SerializedName("createdAt")   val createdAt: String?,
    @SerializedName("updatedAt")   val updatedAt: String?,
)

data class FavoriteListDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("count")  val count: Int,
    @SerializedName("items")  val items: List<FavoriteDto>,
)
