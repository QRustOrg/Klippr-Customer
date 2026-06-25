package com.example.klippr.redemption.data.remote.dto

import com.google.gson.annotations.SerializedName

// @author Samuel Bonifacio
/** Cuerpo de POST /api/redemptions (generar código de canje). */
data class RedeemPromotionRequestDto(
    @SerializedName("consumerId")            val consumerId: String,
    @SerializedName("businessId")            val businessId: String,
    @SerializedName("promotionId")           val promotionId: String,
    @SerializedName("expiresAt")             val expiresAt: String,
    @SerializedName("discountAppliedAmount") val discountAppliedAmount: Double,
    @SerializedName("validationMethod")      val validationMethod: String = "QrScan",
)

/** Cuerpo de POST /api/redemptions/{id}/confirm (marca un canje como usado). */
data class ConfirmRedemptionRequestDto(
    @SerializedName("businessId")       val businessId: String,
    @SerializedName("validationMethod") val validationMethod: String = "ManualCode",
    @SerializedName("confirmedAt")      val confirmedAt: String,
)

/**
 * Respuesta de redención. El schema del backend no está documentado, por lo que todos los campos
 * son opcionales y se aceptan nombres alternativos donde es razonable. El mapper normaliza.
 */
data class RedemptionDto(
    @SerializedName(value = "id", alternate = ["redemptionId"]) val id: String?,
    @SerializedName("code")                  val code: String?,
    @SerializedName(value = "token", alternate = ["uniqueToken"]) val token: String?,
    @SerializedName("status")                val status: String?,
    @SerializedName("promotionId")           val promotionId: String?,
    @SerializedName("businessId")            val businessId: String?,
    @SerializedName("consumerId")            val consumerId: String?,
    @SerializedName("expiresAt")             val expiresAt: String?,
    @SerializedName(value = "confirmedAt", alternate = ["redeemedAt"]) val confirmedAt: String?,
    @SerializedName("blockedAt")             val blockedAt: String?,
    @SerializedName("discountAppliedAmount") val discountAppliedAmount: Double?,
    // Resumen de la promo, si el backend lo embebe
    @SerializedName(value = "businessName", alternate = ["business"])       val businessName: String?,
    @SerializedName(value = "promotionTitle", alternate = ["title"])        val promotionTitle: String?,
    @SerializedName(value = "discountAmount", alternate = ["discountValue"]) val discountValue: Double?,
    @SerializedName("discountType")          val discountType: String?,
)
