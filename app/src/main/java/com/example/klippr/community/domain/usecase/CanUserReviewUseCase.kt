package com.example.klippr.community.domain.usecase

import com.example.klippr.community.domain.repository.ReviewRepository

class CanUserReviewUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(promotionId: String, userId: String): Boolean =
        repository.canUserReview(promotionId, userId)
}