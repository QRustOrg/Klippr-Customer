package com.example.klippr.redemption.data.remote.api

import com.example.klippr.redemption.data.remote.dto.ConfirmRedemptionRequestDto
import com.example.klippr.redemption.data.remote.dto.RedeemPromotionRequestDto
import com.example.klippr.redemption.data.remote.dto.RedemptionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// @author Samuel Bonifacio
/**
 * Contrato Retrofit para el BC Redemption. Requiere token (endpoints autenticados).
 *
 * TS-01: `POST /api/redemptions` es la API oficial de generación de QR de canje:
 * el backend crea la redención y devuelve el token/qrContent que la UI renderiza como bitmap.
 */
interface RedemptionApiService {

    /** Genera la redención y el contenido escaneable del QR para una promoción. */
    @POST("api/redemptions")
    suspend fun generate(@Body body: RedeemPromotionRequestDto): RedemptionDto

    @POST("api/redemptions/{id}/confirm")
    suspend fun confirm(@Path("id") id: String, @Body body: ConfirmRedemptionRequestDto): RedemptionDto

    @GET("api/redemptions/{id}")
    suspend fun getById(@Path("id") id: String): RedemptionDto

    @GET("api/redemptions/consumers/{consumerId}")
    suspend fun getByConsumer(@Path("consumerId") consumerId: String): List<RedemptionDto>
}
