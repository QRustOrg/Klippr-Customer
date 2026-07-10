package com.example.klippr.community.data.repository

import com.example.klippr.community.data.local.dao.ReviewDao
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
        val dto = safeApiCall { api.postReview(PostReviewRequest(promotionId, rating, comment)) }
        dao.insert(dto.toEntity())
        Result.success(dto.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun toggleLike(reviewId: String): Result<Unit> = try {
        // El endpoint devuelve 200 sin body; recargamos del servidor para traer
        // likeCount + likedByCurrentUser reales. El Flow de Room re-emite solo.
        // ponytail: refreshAll re-baja toda la lista por like; si el feed crece,
        // que el endpoint like devuelva el ReviewResource y mapear solo ese.
        api.toggleLike(reviewId)
        refreshAll()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getComments(reviewId: String): Result<List<ReviewComment>> = try {
        val comments = safeApiCall { api.getComments(reviewId) }.map { it.toDomain(reviewId) }
        Result.success(comments)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun postComment(reviewId: String, comment: String): Result<Unit> = try {
        // El endpoint devuelve 200 sin body; el caller recarga con getComments.
        safeApiCall { api.postComment(reviewId, PostCommentRequest(comment)) }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun canUserReview(promotionId: String, userId: String): Boolean = try {
        api.canUserReview(promotionId, userId)
    } catch (e: Exception) {
        false // Si no se puede verificar, no se permite reseñar.
    }

    override suspend fun refreshAll() {
        try {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.insertAll(dtos.map { it.toEntity() })
        } catch (e: Exception) {
            // Sin fallback: se conserva el cache existente (puede quedar vacío). Honesto.
        }
    }
}
