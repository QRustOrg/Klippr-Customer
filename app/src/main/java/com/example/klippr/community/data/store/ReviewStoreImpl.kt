package com.example.klippr.community.data.store

import com.example.klippr.community.data.local.dao.ReviewDao
import com.example.klippr.community.data.local.entity.ReviewEntity
import com.example.klippr.community.data.network.ReviewWebService
import com.example.klippr.community.domain.model.CommentResource
import com.example.klippr.community.domain.model.PostCommentRequest
import com.example.klippr.community.domain.model.PostReviewRequest
import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment
import com.example.klippr.community.domain.model.ReviewResource
import com.example.klippr.shared.data.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Implementacion de [ReviewStore]: combina cache local (Room) y backend (Retrofit).
 * El mapeo recurso/entidad/dominio se hace en linea (sin capa mapper aparte).
 */
class ReviewStoreImpl(
    private val webService: ReviewWebService,
    private val dao: ReviewDao,
) : ReviewStore {

    override fun getAll(): Flow<List<Review>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByPromotion(promotionId: String): Flow<List<Review>> =
        dao.getByPromotion(promotionId).map { list -> list.map { it.toDomain() } }

    override fun getByUser(userId: String): Flow<List<Review>> =
        dao.getByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun postReview(
        promotionId: String,
        rating: Int,
        comment: String,
    ): Result<Review> = try {
        val resource = safeApiCall { webService.postReview(PostReviewRequest(promotionId, rating, comment)) }
        dao.insert(resource.toEntity())
        Result.success(resource.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun toggleLike(reviewId: String): Result<Unit> = try {
        // El endpoint devuelve 200 sin body; recargamos del servidor para traer
        // likeCount + likedByCurrentUser reales. El Flow de Room re-emite solo.
        webService.toggleLike(reviewId)
        refreshAll()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getComments(reviewId: String): Result<List<ReviewComment>> = try {
        val comments = safeApiCall { webService.getComments(reviewId) }.map { it.toDomain(reviewId) }
        Result.success(comments)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun postComment(reviewId: String, comment: String): Result<Unit> = try {
        safeApiCall { webService.postComment(reviewId, PostCommentRequest(comment)) }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun canUserReview(promotionId: String, userId: String): Boolean = try {
        webService.canUserReview(promotionId, userId)
    } catch (e: Exception) {
        false // Si no se puede verificar, no se permite resenar.
    }

    override suspend fun refreshAll() {
        try {
            val resources = webService.getAll()
            dao.deleteAll()
            dao.insertAll(resources.map { it.toEntity() })
        } catch (e: Exception) {
            // Sin fallback: se conserva el cache existente (puede quedar vacio).
        }
    }

    // ── Mapeo en linea ────────────────────────────────────────────────────────
    private fun ReviewResource.toEntity() = ReviewEntity(
        id = id,
        promotionId = promotionId,
        promotionTitle = promotionTitle,
        promotionImageUrl = promotionImageUrl.orEmpty(),
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

    private fun ReviewEntity.toDomain() = Review(
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

    private fun ReviewResource.toDomain() = Review(
        id = id,
        promotionId = promotionId,
        promotionTitle = promotionTitle,
        promotionImageUrl = promotionImageUrl.orEmpty(),
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

    private fun CommentResource.toDomain(fallbackReviewId: String) = ReviewComment(
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
}
