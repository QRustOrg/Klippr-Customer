package com.example.klippr.community.data.remote.api

import com.example.klippr.community.data.remote.dto.CommentDto
import com.example.klippr.community.data.remote.dto.PostCommentRequest
import com.example.klippr.community.data.remote.dto.PostReviewRequest
import com.example.klippr.community.data.remote.dto.ReviewDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApiService {

    @GET("api/reviews")
    suspend fun getAll(): List<ReviewDto>

    @GET("api/reviews")
    suspend fun getByPromotion(
        @Query("promotionId") promotionId: String
    ): List<ReviewDto>

    @GET("api/reviews")
    suspend fun getByUser(
        @Query("userId") userId: String
    ): List<ReviewDto>

    @POST("api/reviews")
    suspend fun postReview(@Body request: PostReviewRequest): ReviewDto

    @GET("api/reviews/can-review")
    suspend fun canUserReview(
        @Query("promotionId") promotionId: String,
        @Query("userId") userId: String
    ): Boolean

    @POST("api/reviews/{id}/like")
    suspend fun toggleLike(@Path("id") reviewId: String): ReviewDto

    @GET("api/reviews/{id}/comments")
    suspend fun getComments(@Path("id") reviewId: String): List<CommentDto>

    @POST("api/reviews/{id}/comments")
    suspend fun postComment(
        @Path("id") reviewId: String,
        @Body request: PostCommentRequest
    ): CommentDto
}
