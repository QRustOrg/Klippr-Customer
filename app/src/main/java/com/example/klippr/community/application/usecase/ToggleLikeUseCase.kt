package com.example.klippr.community.application.usecase

import com.example.klippr.community.data.store.ReviewStore

class ToggleLikeUseCase(private val repository: ReviewStore) {
    suspend operator fun invoke(reviewId: String): Result<Unit> =
        repository.toggleLike(reviewId)
}
