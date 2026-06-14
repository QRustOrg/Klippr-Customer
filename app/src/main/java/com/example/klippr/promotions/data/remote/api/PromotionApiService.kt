package com.example.klippr.promotions.data.remote.api

import com.example.klippr.promotions.data.remote.dto.PromotionDto
import retrofit2.http.GET
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para el BC Promotions. Base URL: /api/promotions */
interface PromotionApiService {

    @GET("api/promotions")
    suspend fun getAll(): List<PromotionDto>

    @GET("api/promotions/active")
    suspend fun getActive(): List<PromotionDto>

    @GET("api/promotions/{id}")
    suspend fun getById(@Path("id") id: String): PromotionDto

    @GET("api/promotions/businesses/{businessId}")
    suspend fun getByBusiness(@Path("businessId") businessId: String): List<PromotionDto>
}
