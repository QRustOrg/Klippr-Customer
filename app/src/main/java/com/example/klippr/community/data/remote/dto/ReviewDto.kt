package com.example.klippr.community.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")             val id: String,
    @SerializedName("promotionId")    val promotionId: String,
    @SerializedName("promotionTitle") val promotionTitle: String,
    @SerializedName("promotionImage") val promotionImageUrl: String?,
    @SerializedName("businessName")   val businessName: String,
    @SerializedName("userId")         val userId: String,
    @SerializedName("userName")       val userName: String,
    @SerializedName("userAvatar")     val userAvatarUrl: String?,
    @SerializedName("rating")         val rating: Int,
    @SerializedName("comment")        val comment: String,
    @SerializedName("createdAt")      val createdAt: Long,
    @SerializedName("verified")       val isVerifiedPurchase: Boolean,
    @SerializedName("likeCount")      val likeCount: Int = 0,
    @SerializedName("likedByCurrentUser") val likedByCurrentUser: Boolean = false,
)

data class PostReviewRequest(
    @SerializedName("promotionId") val promotionId: String,
    @SerializedName("rating")      val rating: Int,
    @SerializedName("comment")     val comment: String
)
