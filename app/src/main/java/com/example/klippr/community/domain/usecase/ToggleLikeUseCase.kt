package com.example.klippr.community.domain.usecase

import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.repository.ReviewRepository

class ToggleLikeUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(reviewId: String): Result<Review> =
        repository.toggleLike(reviewId)
}
