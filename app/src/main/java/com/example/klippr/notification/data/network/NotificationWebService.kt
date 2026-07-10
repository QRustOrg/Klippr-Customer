package com.example.klippr.notification.data.network

import com.example.klippr.notification.data.remote.dto.CreateNotificationBody
import com.example.klippr.notification.data.remote.dto.NotificationResource
import com.google.gson.JsonElement
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Contrato Retrofit del tag Notifications del backend.
// Paths: /api/v1/Notifications/*
interface NotificationWebService {

    // Respuesta flexible: array u objeto con items.
    @GET("api/v1/Notifications/user/{userId}")
    suspend fun getByUser(
        @Path("userId") userId: String,
        @Query("unreadOnly") unreadOnly: Boolean = false,
    ): JsonElement

    @POST("api/v1/Notifications")
    suspend fun create(@Body body: CreateNotificationBody): NotificationResource

    @PATCH("api/v1/Notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: String)

    @PATCH("api/v1/Notifications/user/{userId}/read-all")
    suspend fun markAllAsRead(@Path("userId") userId: String)

    @DELETE("api/v1/Notifications/{notificationId}")
    suspend fun delete(@Path("notificationId") notificationId: String)
}
