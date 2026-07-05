package com.example.klippr.community.presentation.viewmodel

import com.example.klippr.community.application.usecase.CanUserReviewUseCase
import com.example.klippr.community.application.usecase.GetAllReviewsUseCase
import com.example.klippr.community.application.usecase.GetReviewCommentsUseCase
import com.example.klippr.community.application.usecase.PostReviewCommentUseCase
import com.example.klippr.community.application.usecase.PostReviewUseCase
import com.example.klippr.community.application.usecase.ToggleLikeUseCase
import com.example.klippr.community.data.store.ReviewStore
import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun toggleLike_updatesVisibleReviewReactionState() = runTest(dispatcher) {
        val store = FakeReviewStore()
        val viewModel = viewModel(store)
        advanceUntilIdle()

        viewModel.toggleLike("review-1")
        advanceUntilIdle()

        val review = viewModel.uiState.value.reviews.single()
        assertEquals(listOf("review-1"), store.toggleLikeCalls)
        assertEquals(1, review.likeCount)
        assertEquals(true, review.isLikedByCurrentUser)
    }

    private fun viewModel(store: ReviewStore) = CommunityViewModel(
        getAllReviewsUseCase = GetAllReviewsUseCase(store),
        postReviewUseCase = PostReviewUseCase(store),
        canUserReviewUseCase = CanUserReviewUseCase(store),
        toggleLikeUseCase = ToggleLikeUseCase(store),
        getReviewCommentsUseCase = GetReviewCommentsUseCase(store),
        postReviewCommentUseCase = PostReviewCommentUseCase(store),
    )

    private class FakeReviewStore : ReviewStore {
        private val reviews = MutableStateFlow(listOf(review()))
        val toggleLikeCalls = mutableListOf<String>()

        override fun getAll(): Flow<List<Review>> = reviews
        override fun getByPromotion(promotionId: String): Flow<List<Review>> = reviews
        override fun getByUser(userId: String): Flow<List<Review>> = reviews
        override suspend fun postReview(promotionId: String, rating: Int, comment: String): Result<Review> =
            Result.success(review())
        override suspend fun canUserReview(promotionId: String, userId: String): Boolean = true
        override suspend fun toggleLike(reviewId: String): Result<Unit> {
            toggleLikeCalls += reviewId
            reviews.value = reviews.value.map {
                if (it.id == reviewId) it.copy(likeCount = 1, isLikedByCurrentUser = true) else it
            }
            return Result.success(Unit)
        }
        override suspend fun getComments(reviewId: String): Result<List<ReviewComment>> =
            Result.success(emptyList())
        override suspend fun postComment(reviewId: String, comment: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun refreshAll() = Unit
    }
}

private fun review() = Review(
    id = "review-1",
    promotionId = "promo-1",
    promotionTitle = "Promo",
    promotionImageUrl = "",
    businessName = "Negocio",
    userId = "user-1",
    userName = "Usuario",
    userAvatarUrl = null,
    rating = 5,
    comment = "Buena promo",
    createdAt = 1_718_000_000_000,
    isVerifiedPurchase = true,
)
