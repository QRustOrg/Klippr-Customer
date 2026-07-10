package com.example.klippr.community.application.usecase

import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.data.store.ReviewStore
import kotlinx.coroutines.flow.Flow

class GetAllReviewsUseCase(private val repository: ReviewStore) {
    operator fun invoke(): Flow<List<Review>> = repository.getAll()

    suspend fun refresh() = repository.refreshAll()
}
