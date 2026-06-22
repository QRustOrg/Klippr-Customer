package com.example.klippr.favorites.data.remote.api

import com.example.klippr.favorites.data.remote.dto.FavoriteDto
import com.example.klippr.favorites.data.remote.dto.FavoriteListDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoriteApiService{
    @GET("api/v1/Favorites/user/{userId}")
    suspend fun getByUser(
        @Path("userId") userId: String,
        @Query("archived") archived: Boolean = false,
    ): FavoriteListDto

    @GET("api/v1/Favorites/{id}")
    suspend fun getById(@Path("id") id: String): FavoriteDto

    @POST("api/v1/Favorites")
    suspend fun save(@Body body: Map<String, String>): FavoriteDto

    @DELETE("api/v1/Favorites/{favoriteId}")
    suspend fun delete(
        @Path("favoriteId") favoriteId: String,
        @Query("userId") userId: String,
    ): Unit

    @PATCH("api/v1/Favorites/{favoriteId}/archive")
    suspend fun archive(
        @Path("favoriteId") favoriteId: String,
        @Query("userId") userId: String,
    ): Unit

    @PATCH("api/v1/Favorites/{favoriteId}/restore")
    suspend fun restore(
        @Path("favoriteId") favoriteId: String,
        @Query("userId") userId: String,
    ): Unit
}
