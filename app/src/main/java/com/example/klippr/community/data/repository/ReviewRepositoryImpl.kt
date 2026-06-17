package com.example.klippr.community.data.repository

import com.example.klippr.community.data.local.dao.ReviewDao
import com.example.klippr.community.data.local.entity.ReviewEntity
import com.example.klippr.community.data.mapper.toDomain
import com.example.klippr.community.data.mapper.toEntity
import com.example.klippr.community.data.remote.api.ReviewApiService
import com.example.klippr.community.data.remote.dto.PostCommentRequest
import com.example.klippr.community.data.remote.dto.PostReviewRequest
import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment
import com.example.klippr.community.domain.repository.ReviewRepository
import com.example.klippr.core.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ReviewRepositoryImpl(
    private val api: ReviewApiService,
    private val dao: ReviewDao
) : ReviewRepository {

    override fun getAll(): Flow<List<Review>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByPromotion(promotionId: String): Flow<List<Review>> =
        dao.getByPromotion(promotionId).map { list -> list.map { it.toDomain() } }

    override fun getByUser(userId: String): Flow<List<Review>> =
        dao.getByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun postReview(
        promotionId: String,
        rating: Int,
        comment: String
    ): Result<Review> = try {
        val dto = api.postReview(PostReviewRequest(promotionId, rating, comment))
        dao.insert(dto.toEntity())
        Result.success(dto.toDomain())
    } catch (e: Exception) {
        // Fallback mock mientras no hay backend
        val mockReview = ReviewEntity(
            id = UUID.randomUUID().toString(),
            promotionId = promotionId,
            promotionTitle = "Promoción #$promotionId",
            promotionImageUrl = "",
            businessName = "Tu negocio",
            userId = "current_user",
            userName = "Tú",
            userAvatarUrl = null,
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis(),
            isVerifiedPurchase = true
        )
        dao.insert(mockReview)
        Result.success(mockReview.toDomain())
    }

    override suspend fun toggleLike(reviewId: String): Result<Review> = try {
        val dto = api.toggleLike(reviewId)
        dao.insert(dto.toEntity())
        Result.success(dto.toDomain())
    } catch (e: Exception) {
        val current = dao.getById(reviewId)
        if (current != null) {
            val nowLiked = !current.isLikedByCurrentUser
            val newCount = if (nowLiked) current.likeCount + 1 else maxOf(0, current.likeCount - 1)
            dao.updateLike(reviewId, newCount, nowLiked)
            Result.success(current.copy(likeCount = newCount, isLikedByCurrentUser = nowLiked).toDomain())
        } else {
            Result.failure(Exception("Reseña no encontrada"))
        }
    }

    override suspend fun getComments(reviewId: String): Result<List<ReviewComment>> = try {
        val comments = safeApiCall { api.getComments(reviewId) }.map { it.toDomain(reviewId) }
        Result.success(comments)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun postComment(reviewId: String, comment: String): Result<ReviewComment> = try {
        val created = safeApiCall { api.postComment(reviewId, PostCommentRequest(comment)) }.toDomain(reviewId)
        Result.success(created)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun canUserReview(promotionId: String, userId: String): Boolean = try {
        api.canUserReview(promotionId, userId)
    } catch (e: Exception) {
        true // Mock: permite siempre mientras no hay backend
    }

    override suspend fun refreshAll() {
        try {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.insertAll(dtos.map { it.toEntity() })
        } catch (e: Exception) {
            seedMockDataIfEmpty()
        }
    }

    // ─── Mock data para desarrollo sin backend ───────────────────────────────
    private suspend fun seedMockDataIfEmpty() {
        val mockReviews = listOf(
            ReviewEntity(
                id = "r1",
                promotionId = "p1",
                promotionTitle = "2x1 en Café Americano",
                promotionImageUrl = "https://picsum.photos/seed/cafe/400/200",
                businessName = "Café Barista",
                userId = "u1",
                userName = "María G.",
                userAvatarUrl = null,
                rating = 5,
                comment = "¡Excelente! El café estaba delicioso y el trato fue muy amable. Definitivamente volvería.",
                createdAt = System.currentTimeMillis() - 86400000L,
                isVerifiedPurchase = true
            ),
            ReviewEntity(
                id = "r2",
                promotionId = "p2",
                promotionTitle = "20% off en Burger Clásica",
                promotionImageUrl = "https://picsum.photos/seed/burger/400/200",
                businessName = "Fast Food El Rey",
                userId = "u2",
                userName = "Carlos M.",
                userAvatarUrl = null,
                rating = 4,
                comment = "Muy buena hamburguesa, el descuento se aplicó sin problemas. El local estaba un poco lleno.",
                createdAt = System.currentTimeMillis() - 172800000L,
                isVerifiedPurchase = true
            ),
            ReviewEntity(
                id = "r3",
                promotionId = "p1",
                promotionTitle = "2x1 en Café Americano",
                promotionImageUrl = "https://picsum.photos/seed/cafe/400/200",
                businessName = "Café Barista",
                userId = "u3",
                userName = "Ana P.",
                userAvatarUrl = null,
                rating = 3,
                comment = "El café está rico pero tuvimos que esperar bastante. La promo se aplicó bien.",
                createdAt = System.currentTimeMillis() - 259200000L,
                isVerifiedPurchase = true
            ),
            ReviewEntity(
                id = "r4",
                promotionId = "p3",
                promotionTitle = "Combo Familiar -30%",
                promotionImageUrl = "https://picsum.photos/seed/pizza/400/200",
                businessName = "Pizza Nostra",
                userId = "u4",
                userName = "Luis R.",
                userAvatarUrl = null,
                rating = 5,
                comment = "Increíble relación calidad-precio. La pizza llegó caliente y el descuento fue enorme.",
                createdAt = System.currentTimeMillis() - 345600000L,
                isVerifiedPurchase = true
            )
        )
        dao.insertAll(mockReviews)
    }
}
