package com.example.klippr.community.data.mapper

import com.example.klippr.community.data.local.entity.ReviewEntity
import com.example.klippr.community.data.remote.dto.ReviewDto
import com.example.klippr.community.domain.model.Review

fun ReviewDto.toEntity() = ReviewEntity(
    id = id,
    promotionId = promotionId,
    promotionTitle = promotionTitle,
    promotionImageUrl = promotionImageUrl,
    businessName = businessName,
    userId = userId,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    comment = comment,
    createdAt = createdAt,
    isVerifiedPurchase = isVerifiedPurchase,
    likeCount = likeCount,
    isLikedByCurrentUser = false,
)

// Entity → Domain
fun ReviewEntity.toDomain() = Review(
    id = id,
    promotionId = promotionId,
    promotionTitle = promotionTitle,
    promotionImageUrl = promotionImageUrl,
    businessName = businessName,
    userId = userId,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    comment = comment,
    createdAt = createdAt,
    isVerifiedPurchase = isVerifiedPurchase,
    likeCount = likeCount,
    isLikedByCurrentUser = isLikedByCurrentUser,
)

// DTO → Domain (directo, sin cache)
fun ReviewDto.toDomain() = Review(
    id = id,
    promotionId = promotionId,
    promotionTitle = promotionTitle,
    promotionImageUrl = promotionImageUrl,
    businessName = businessName,
    userId = userId,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    comment = comment,
    createdAt = createdAt,
    isVerifiedPurchase = isVerifiedPurchase,
    likeCount = likeCount,
    isLikedByCurrentUser = false,
)
