package com.example.klippr.community.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.klippr.community.domain.usecase.CanUserReviewUseCase
import com.example.klippr.community.domain.usecase.GetAllReviewsUseCase
import com.example.klippr.community.domain.usecase.PostReviewUseCase
import com.example.klippr.community.presentation.state.CommunityUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val getAllReviewsUseCase: GetAllReviewsUseCase,
    private val postReviewUseCase: PostReviewUseCase,
    private val canUserReviewUseCase: CanUserReviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAllReviewsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { reviews ->
                    _uiState.update { it.copy(isLoading = false, reviews = reviews) }
                }
        }
    }

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

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Factory para instanciar con dependencias
    class Factory(
        private val getAllReviewsUseCase: GetAllReviewsUseCase,
        private val postReviewUseCase: PostReviewUseCase,
        private val canUserReviewUseCase: CanUserReviewUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CommunityViewModel(getAllReviewsUseCase, postReviewUseCase, canUserReviewUseCase) as T
    }
}