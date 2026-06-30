package com.example.klippr.favorites.data.network

import com.example.klippr.favorites.domain.model.FavoriteResource
import com.example.klippr.favorites.domain.model.FavoriteListResource
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoriteWebService{
    @GET("api/v1/Favorites/user/{userId}")
    suspend fun getByUser(
        @Path("userId") userId: String,
        @Query("archived") archived: Boolean = false,
    ): FavoriteListResource

    @GET("api/v1/Favorites/{id}")
    suspend fun getById(@Path("id") id: String): FavoriteResource

    @POST("api/v1/Favorites")
    suspend fun save(@Body body: Map<String, String>): FavoriteResource

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
