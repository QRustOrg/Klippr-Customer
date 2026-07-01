package com.example.klippr.profile.data.network

import com.example.klippr.profile.domain.model.UserPreference
import com.example.klippr.profile.domain.model.UserPreferenceRequest
import com.example.klippr.profile.domain.model.UserResource
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para datos de usuario/perfil. Requiere Bearer. */
interface ProfileWebService {

    @GET("api/Users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserResource

    @GET("api/v1/Preferences")
    suspend fun getPreferences(): List<UserPreference>

    @POST("api/v1/Preferences")
    suspend fun createPreference(@Body preference: UserPreferenceRequest): UserPreference

    @PUT("api/v1/Preferences/{preferenceId}")
    suspend fun updatePreference(
        @Path("preferenceId") preferenceId: Int,
        @Body preference: UserPreferenceRequest,
    ): UserPreference
}
