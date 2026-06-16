package com.example.klippr.core.network

import com.example.klippr.community.data.remote.api.ReviewApiService
import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.iam.data.remote.api.AuthApiService
import com.example.klippr.profile.data.remote.api.ProfileApiService
import com.example.klippr.promotions.data.remote.api.PromotionApiService
import com.example.klippr.redemption.data.remote.api.RedemptionApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// @author Samuel Bonifacio
/**
 * Construye un único Retrofit con OkHttp compartido (token Bearer + logging).
 * Todos los BC obtienen sus servicios desde aquí para reutilizar cliente y base URL.
 */
class NetworkModule(sessionStore: SessionDataStore) {

    private val baseUrl = "https://klippr-backend-production.up.railway.app/"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionStore))
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC },
        )
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService           = retrofit.create(AuthApiService::class.java)
    val profileApi: ProfileApiService     = retrofit.create(ProfileApiService::class.java)
    val promotionApi: PromotionApiService = retrofit.create(PromotionApiService::class.java)
    val redemptionApi: RedemptionApiService = retrofit.create(RedemptionApiService::class.java)
    val reviewApi: ReviewApiService       = retrofit.create(ReviewApiService::class.java)  // ← AGREGAR
}