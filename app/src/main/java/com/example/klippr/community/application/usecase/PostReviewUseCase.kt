package com.example.klippr.community.application.usecase

import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.data.store.ReviewStore

class PostReviewUseCase(private val repository: ReviewStore) {
    suspend operator fun invoke(
        promotionId: String,
        rating: Int,
        comment: String
    ): Result<Review> {
        if (rating < 1 || rating > 5) return Result.failure(
            IllegalArgumentException("El rating debe estar entre 1 y 5")
        )
        if (comment.isBlank()) return Result.failure(
            IllegalArgumentException("El comentario no puede estar vacío")
        )
        return repository.postReview(promotionId, rating, comment)
    }
}