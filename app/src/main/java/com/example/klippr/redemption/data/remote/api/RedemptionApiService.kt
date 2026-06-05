package com.example.klippr.redemption.data.remote.api

import com.example.klippr.redemption.data.remote.dto.RedeemPromotionRequestDto
import com.example.klippr.redemption.data.remote.dto.RedemptionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para el BC Redemption. Requiere token (endpoints autenticados). */
interface RedemptionApiService {

    @POST("api/redemptions")
    suspend fun generate(@Body body: RedeemPromotionRequestDto): RedemptionDto

    @GET("api/redemptions/{id}")
    suspend fun getById(@Path("id") id: String): RedemptionDto

    @GET("api/redemptions/consumers/{consumerId}")
    suspend fun getByConsumer(@Path("consumerId") consumerId: String): List<RedemptionDto>
}
