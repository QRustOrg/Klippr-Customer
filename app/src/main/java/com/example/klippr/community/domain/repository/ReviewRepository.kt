package com.example.klippr.community.domain.repository

import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    fun getAll(): Flow<List<Review>>

    fun getByPromotion(promotionId: String): Flow<List<Review>>

    fun getByUser(userId: String): Flow<List<Review>>

    suspend fun postReview(
        promotionId: String,
        rating: Int,
        comment: String
    ): Result<Review>

    suspend fun canUserReview(promotionId: String, userId: String): Boolean

    suspend fun toggleLike(reviewId: String): Result<Review>

    suspend fun getComments(reviewId: String): Result<List<ReviewComment>>

    suspend fun postComment(reviewId: String, comment: String): Result<ReviewComment>

    suspend fun refreshAll()
}
