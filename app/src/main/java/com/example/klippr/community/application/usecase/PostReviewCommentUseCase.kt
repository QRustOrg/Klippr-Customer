package com.example.klippr.community.application.usecase

import com.example.klippr.community.data.store.ReviewStore

class PostReviewCommentUseCase(private val repository: ReviewStore) {
    suspend operator fun invoke(reviewId: String, comment: String) =
        repository.postComment(reviewId, comment.trim())
}
