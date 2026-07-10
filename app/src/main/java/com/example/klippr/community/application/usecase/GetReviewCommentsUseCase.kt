package com.example.klippr.community.application.usecase

import com.example.klippr.community.data.store.ReviewStore

class GetReviewCommentsUseCase(private val repository: ReviewStore) {
    suspend operator fun invoke(reviewId: String) = repository.getComments(reviewId)
}
