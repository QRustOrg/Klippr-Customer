package com.example.klippr.redemption.data.network

import com.example.klippr.redemption.domain.model.ConfirmRedemptionRequest
import com.example.klippr.redemption.domain.model.RedeemPromotionRequest
import com.example.klippr.redemption.domain.model.RedemptionResource
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para el BC Redemption. Requiere token (endpoints autenticados). */
interface RedemptionWebService {

    @POST("api/redemptions")
    suspend fun generate(@Body body: RedeemPromotionRequest): RedemptionResource

    @POST("api/redemptions/{id}/confirm")
    suspend fun confirm(@Path("id") id: String, @Body body: ConfirmRedemptionRequest): RedemptionResource

    @GET("api/redemptions/{id}")
    suspend fun getById(@Path("id") id: String): RedemptionResource

    @GET("api/redemptions/consumers/{consumerId}")
    suspend fun getByConsumer(@Path("consumerId") consumerId: String): List<RedemptionResource>
}
