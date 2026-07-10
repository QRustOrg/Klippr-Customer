package com.example.klippr.promotions.data.network

import com.example.klippr.promotions.domain.model.PromotionResource
import retrofit2.http.GET
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para el BC Promotions. Base URL: /api/promotions */
interface PromotionWebService {

    @GET("api/promotions")
    suspend fun getAll(): List<PromotionResource>

    @GET("api/promotions/active")
    suspend fun getActive(): List<PromotionResource>

    @GET("api/promotions/{id}")
    suspend fun getById(@Path("id") id: String): PromotionResource

    @GET("api/promotions/businesses/{businessId}")
    suspend fun getByBusiness(@Path("businessId") businessId: String): List<PromotionResource>
}
