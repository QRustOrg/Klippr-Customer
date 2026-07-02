package com.example.klippr.preferences.data.network

import com.example.klippr.preferences.domain.model.UserPreference
import com.example.klippr.preferences.domain.model.UserPreferenceRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para `/api/v1/Preferences`. Requiere Bearer. */
interface PreferenceWebService {

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
