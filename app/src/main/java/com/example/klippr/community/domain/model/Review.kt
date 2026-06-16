package com.example.klippr.community.domain.model

data class Review(
    val id: String,
    val promotionId: String,
    val promotionTitle: String,
    val promotionImageUrl: String,
    val businessName: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val rating: Int,
    val comment: String,
    val createdAt: Long,
    val isVerifiedPurchase: Boolean,
    val likeCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
)