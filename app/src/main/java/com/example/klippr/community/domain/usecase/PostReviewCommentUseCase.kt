package com.example.klippr.community.domain.usecase

import com.example.klippr.community.domain.repository.ReviewRepository

class PostReviewCommentUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(reviewId: String, comment: String) =
        repository.postComment(reviewId, comment.trim())
}
