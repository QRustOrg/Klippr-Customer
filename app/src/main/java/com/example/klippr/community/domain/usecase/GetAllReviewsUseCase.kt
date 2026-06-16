package com.example.klippr.community.domain.usecase

import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow

class GetAllReviewsUseCase(private val repository: ReviewRepository) {
    operator fun invoke(): Flow<List<Review>> = repository.getAll()
}