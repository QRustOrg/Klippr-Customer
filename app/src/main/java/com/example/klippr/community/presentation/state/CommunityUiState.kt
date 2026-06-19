package com.example.klippr.community.presentation.state

import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment

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
    val submitSuccess: Boolean = false,

    // Estado de comentarios sobre una publicacion/review (US-15)
    val isCommentSheetOpen: Boolean = false,
    val selectedReviewId: String? = null,
    val selectedReviewTitle: String? = null,
    val commentsByReviewId: Map<String, List<ReviewComment>> = emptyMap(),
    val draftReplyComment: String = "",
    val isLoadingComments: Boolean = false,
    val isSubmittingComment: Boolean = false,
)
