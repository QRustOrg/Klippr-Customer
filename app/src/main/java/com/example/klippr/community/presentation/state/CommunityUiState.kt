package com.example.klippr.community.presentation.state

import com.example.klippr.community.domain.model.Review

data class CommunityUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Estado del bottom sheet para escribir reseña
    val isReviewSheetOpen: Boolean = false,
    val selectedPromotionId: String? = null,
    val selectedPromotionTitle: String? = null,
    val canCurrentUserReview: Boolean = false,

    // Formulario de nueva reseña
    val draftRating: Int = 0,
    val draftComment: String = "",
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)