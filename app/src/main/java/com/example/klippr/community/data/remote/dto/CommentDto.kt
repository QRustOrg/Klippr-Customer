package com.example.klippr.community.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("reviewId") val reviewId: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("userName") val userName: String? = null,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
)

data class PostCommentRequest(
    @SerializedName("comment") val comment: String,
)
