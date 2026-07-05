package com.example.klippr.community.data.repository

import com.example.klippr.community.data.local.dao.ReviewDao
import com.example.klippr.community.data.local.entity.ReviewEntity
import com.example.klippr.community.data.remote.api.ReviewApiService
import com.example.klippr.community.data.remote.dto.CommentDto
import com.example.klippr.community.data.remote.dto.PostCommentRequest
import com.example.klippr.community.data.remote.dto.PostReviewRequest
import com.example.klippr.community.data.remote.dto.ReviewDto
import com.example.klippr.core.network.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ReviewRepositoryImplTest {

    @Test
    fun postReview_mapsUnauthorizedToApiException() = runBlocking {
        val repository = ReviewRepositoryImpl(UnauthorizedReviewApiService(), NoopReviewDao())

        val result = repository.postReview(
            promotionId = "promo-1",
            rating = 5,
            comment = "Muy bueno",
        )

        val exception = result.exceptionOrNull()
        assertTrue(exception is ApiException)
        assertEquals("Tu sesión expiró. Inicia sesión nuevamente.", exception?.message)
    }

    @Test
    fun toggleLike_callsReviewLikeEndpointAndRefreshesCache() = runBlocking {
        val api = TrackingReviewApiService()
        val dao = NoopReviewDao()
        val repository = ReviewRepositoryImpl(api, dao)

        val result = repository.toggleLike("review-1")

        assertTrue(result.isSuccess)
        assertEquals(listOf("review-1"), api.toggleLikeCalls)
        assertEquals(1, api.getAllCalls)
    }

    private class UnauthorizedReviewApiService : ReviewApiService {
        override suspend fun getAll(): List<ReviewDto> = emptyList()
        override suspend fun getByPromotion(promotionId: String): List<ReviewDto> = emptyList()
        override suspend fun getByUser(userId: String): List<ReviewDto> = emptyList()
        override suspend fun postReview(request: PostReviewRequest): ReviewDto {
            throw HttpException(Response.error<Unit>(401, "".toResponseBody()))
        }
        override suspend fun canUserReview(promotionId: String, userId: String): Boolean = false
        override suspend fun toggleLike(reviewId: String) = Unit
        override suspend fun getComments(reviewId: String): List<CommentDto> = emptyList()
        override suspend fun postComment(reviewId: String, request: PostCommentRequest) = Unit
    }

    private class TrackingReviewApiService : ReviewApiService {
        val toggleLikeCalls = mutableListOf<String>()
        var getAllCalls = 0

        override suspend fun getAll(): List<ReviewDto> {
            getAllCalls += 1
            return emptyList()
        }
        override suspend fun getByPromotion(promotionId: String): List<ReviewDto> = emptyList()
        override suspend fun getByUser(userId: String): List<ReviewDto> = emptyList()
        override suspend fun postReview(request: PostReviewRequest): ReviewDto = error("unused")
        override suspend fun canUserReview(promotionId: String, userId: String): Boolean = false
        override suspend fun toggleLike(reviewId: String) {
            toggleLikeCalls += reviewId
        }
        override suspend fun getComments(reviewId: String): List<CommentDto> = emptyList()
        override suspend fun postComment(reviewId: String, request: PostCommentRequest) = Unit
    }

    private class NoopReviewDao : ReviewDao {
        override fun getAll(): Flow<List<ReviewEntity>> = emptyFlow()
        override fun getByPromotion(promotionId: String): Flow<List<ReviewEntity>> = emptyFlow()
        override fun getByUser(userId: String): Flow<List<ReviewEntity>> = emptyFlow()
        override suspend fun getById(id: String): ReviewEntity? = null
        override suspend fun insertAll(reviews: List<ReviewEntity>) = Unit
        override suspend fun insert(review: ReviewEntity) = Unit
        override suspend fun deleteAll() = Unit
        override suspend fun updateLike(id: String, count: Int, liked: Boolean) = Unit
    }
}
