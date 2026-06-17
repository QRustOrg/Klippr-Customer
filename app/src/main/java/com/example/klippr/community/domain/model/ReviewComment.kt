package com.example.klippr.community.domain.model

data class ReviewComment(
    val id: String,
    val reviewId: String,
    val userId: String,
    val userName: String,
    val comment: String,
    val createdAt: Long,
)
