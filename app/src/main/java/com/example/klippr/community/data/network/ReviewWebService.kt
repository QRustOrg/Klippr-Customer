package com.example.klippr.community.data.network

import com.example.klippr.community.domain.model.CommentResource
import com.example.klippr.community.domain.model.PostCommentRequest
import com.example.klippr.community.domain.model.PostReviewRequest
import com.example.klippr.community.domain.model.ReviewResource
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewWebService {

    @GET("api/reviews")
    suspend fun getAll(): List<ReviewResource>

    @GET("api/reviews")
    suspend fun getByPromotion(
        @Query("promotionId") promotionId: String
    ): List<ReviewResource>

    @GET("api/reviews")
    suspend fun getByUser(
        @Query("userId") userId: String
    ): List<ReviewResource>

    @POST("api/reviews")
    suspend fun postReview(@Body request: PostReviewRequest): ReviewResource

    @GET("api/reviews/can-review")
    suspend fun canUserReview(
        @Query("promotionId") promotionId: String,
        @Query("userId") userId: String
    ): Boolean

    @POST("api/reviews/{id}/like")
    suspend fun toggleLike(@Path("id") reviewId: String)

    @GET("api/reviews/{id}/comments")
    suspend fun getComments(@Path("id") reviewId: String): List<CommentResource>

    @POST("api/reviews/{id}/comments")
    suspend fun postComment(
        @Path("id") reviewId: String,
        @Body request: PostCommentRequest
    )
}
