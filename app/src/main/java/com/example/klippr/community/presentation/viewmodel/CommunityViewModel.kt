package com.example.klippr.community.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.application.usecase.CanUserReviewUseCase
import com.example.klippr.community.application.usecase.GetAllReviewsUseCase
import com.example.klippr.community.application.usecase.GetReviewCommentsUseCase
import com.example.klippr.community.application.usecase.PostReviewCommentUseCase
import com.example.klippr.community.application.usecase.PostReviewUseCase
import com.example.klippr.community.application.usecase.ToggleLikeUseCase
import com.example.klippr.community.presentation.state.CommunityUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.klippr.shared.core.ServiceLocator

class CommunityViewModel(
    private val getAllReviewsUseCase: GetAllReviewsUseCase,
    private val postReviewUseCase: PostReviewUseCase,
    private val canUserReviewUseCase: CanUserReviewUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val getReviewCommentsUseCase: GetReviewCommentsUseCase,
    private val postReviewCommentUseCase: PostReviewCommentUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private val _promotionIdFilter = MutableStateFlow<String?>(null)

    init {
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAllReviewsUseCase.refresh()
            combine(
                getAllReviewsUseCase(),
                _promotionIdFilter,
            ) { reviews, filter ->
                if (filter == null) reviews else reviews.filter { review -> review.promotionId == filter }
            }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { reviews ->
                    _uiState.update { it.copy(isLoading = false, reviews = reviews) }
                }
        }
    }

    fun setPromotionFilter(promotionId: String?) {
        _promotionIdFilter.value = promotionId
    }

    // Abre el sheet verificando con el API si el usuario puede reseñar (desde CommunityScreen)
    fun openReviewSheet(promotionId: String, promotionTitle: String, currentUserId: String) {
        viewModelScope.launch {
            val canReview = canUserReviewUseCase(promotionId, currentUserId)
            _uiState.update {
                it.copy(
                    isReviewSheetOpen = true,
                    selectedPromotionId = promotionId,
                    selectedPromotionTitle = promotionTitle,
                    canCurrentUserReview = canReview,
                    draftRating = 0,
                    draftComment = "",
                    submitSuccess = false
                )
            }
        }
    }

    // Abre el sheet desde MisPromos: el canje ya confirma que el usuario puede reseñar
    fun openReviewSheetForRedeemed(promotionId: String, promotionTitle: String) {
        _uiState.update {
            it.copy(
                isReviewSheetOpen = true,
                selectedPromotionId = promotionId,
                selectedPromotionTitle = promotionTitle,
                canCurrentUserReview = true,
                draftRating = 0,
                draftComment = "",
                submitSuccess = false
            )
        }
    }

    fun closeReviewSheet() {
        _uiState.update { it.copy(isReviewSheetOpen = false) }
    }

    fun onRatingChanged(rating: Int) {
        _uiState.update { it.copy(draftRating = rating) }
    }

    fun onCommentChanged(comment: String) {
        _uiState.update { it.copy(draftComment = comment) }
    }

    fun submitReview() {
        val state = _uiState.value
        val promotionId = state.selectedPromotionId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = postReviewUseCase(promotionId, state.draftRating, state.draftComment)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isSubmitting = false, submitSuccess = true, isReviewSheetOpen = false)
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = e.message) }
                }
            )
        }
    }

    // US-16: toggle like en una reseña; Room emite el nuevo estado automáticamente
    fun toggleLike(reviewId: String) {
        viewModelScope.launch {
            toggleLikeUseCase(reviewId).onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun openCommentSheet(review: Review) {
        _uiState.update {
            it.copy(
                isCommentSheetOpen = true,
                selectedReviewId = review.id,
                selectedReviewTitle = review.promotionTitle,
                draftReplyComment = "",
                errorMessage = null,
            )
        }
        loadComments(review.id)
    }

    fun closeCommentSheet() {
        _uiState.update {
            it.copy(
                isCommentSheetOpen = false,
                selectedReviewId = null,
                selectedReviewTitle = null,
                draftReplyComment = "",
                isLoadingComments = false,
                isSubmittingComment = false,
            )
        }
    }

    fun onReplyCommentChanged(comment: String) {
        _uiState.update { it.copy(draftReplyComment = comment) }
    }

    fun loadComments(reviewId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingComments = true, errorMessage = null) }
            getReviewCommentsUseCase(reviewId).fold(
                onSuccess = { comments ->
                    _uiState.update {
                        it.copy(
                            isLoadingComments = false,
                            commentsByReviewId = it.commentsByReviewId + (reviewId to comments),
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingComments = false,
                            errorMessage = e.message ?: "No se pudieron cargar los comentarios",
                        )
                    }
                },
            )
        }
    }

    fun submitComment() {
        val state = _uiState.value
        val reviewId = state.selectedReviewId ?: return
        val comment = state.draftReplyComment.trim()
        if (comment.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Escribe un comentario") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingComment = true, errorMessage = null) }
            postReviewCommentUseCase(reviewId, comment).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isSubmittingComment = false, draftReplyComment = "")
                    }
                    // El POST devuelve 200 sin body; recargamos del servidor.
                    loadComments(reviewId)
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isSubmittingComment = false,
                            errorMessage = e.message ?: "No se pudo registrar el comentario",
                        )
                    }
                },
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = CommunityViewModel(
                    getAllReviewsUseCase = GetAllReviewsUseCase(serviceLocator.reviewStore),
                    postReviewUseCase = PostReviewUseCase(serviceLocator.reviewStore),
                    canUserReviewUseCase = CanUserReviewUseCase(serviceLocator.reviewStore),
                    toggleLikeUseCase = ToggleLikeUseCase(serviceLocator.reviewStore),
                    getReviewCommentsUseCase = GetReviewCommentsUseCase(serviceLocator.reviewStore),
                    postReviewCommentUseCase = PostReviewCommentUseCase(serviceLocator.reviewStore),
                ) as T
            }
    }
}
