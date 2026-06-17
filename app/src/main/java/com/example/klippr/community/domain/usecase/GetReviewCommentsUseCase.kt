package com.example.klippr.community.domain.usecase

import com.example.klippr.community.domain.repository.ReviewRepository

class GetReviewCommentsUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(reviewId: String) = repository.getComments(reviewId)
}
