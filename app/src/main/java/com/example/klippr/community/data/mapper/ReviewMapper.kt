package com.example.klippr.community.data.mapper

import com.example.klippr.community.data.local.entity.ReviewEntity
import com.example.klippr.community.data.remote.dto.CommentDto
import com.example.klippr.community.data.remote.dto.ReviewDto
import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
    isLikedByCurrentUser = likedByCurrentUser,
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
    isLikedByCurrentUser = likedByCurrentUser,
)

fun CommentDto.toDomain(fallbackReviewId: String) = ReviewComment(
    id = id.orEmpty(),
    reviewId = reviewId ?: fallbackReviewId,
    userId = userId.orEmpty(),
    userName = userName?.takeIf { it.isNotBlank() } ?: "Usuario",
    comment = comment.orEmpty(),
    createdAt = createdAt.toEpochMillisOrNow(),
)

private fun String?.toEpochMillisOrNow(): Long {
    val value = this?.takeIf { it.isNotBlank() } ?: return System.currentTimeMillis()
    return runCatching { Instant.parse(value).toEpochMilli() }
        .recoverCatching {
            LocalDateTime.parse(value)
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }
        .getOrDefault(System.currentTimeMillis())
}
