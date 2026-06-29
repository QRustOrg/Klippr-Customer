package com.example.klippr.community.application.usecase

import com.example.klippr.community.data.store.ReviewStore

class CanUserReviewUseCase(private val repository: ReviewStore) {
    suspend operator fun invoke(promotionId: String, userId: String): Boolean =
        repository.canUserReview(promotionId, userId)
}