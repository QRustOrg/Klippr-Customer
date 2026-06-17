package com.example.klippr.promotions.data.remote.dto

import com.google.gson.annotations.SerializedName

// @author Samuel Bonifacio
/** Refleja el JSON de `PromotionResource` del backend. Enums y fechas llegan como String. */
data class PromotionDto(
    @SerializedName("id")             val id: String,
    @SerializedName("businessId")     val businessId: String,
    @SerializedName("title")          val title: String,
    @SerializedName("description")    val description: String,
    @SerializedName("discountAmount") val discountAmount: Double,
    @SerializedName("discountType")   val discountType: String,
    @SerializedName("startDate")      val startDate: String,
    @SerializedName("endDate")        val endDate: String,
    @SerializedName("redemptionCap")  val redemptionCap: Int?,
    @SerializedName("imageKey")       val imageKey: String?,
    @SerializedName("termsAndConditions") val termsAndConditions: String? = null,
    @SerializedName("category")       val category: String? = null,
    @SerializedName("locationName")   val locationName: String? = null,
    @SerializedName("businessName")   val businessName: String? = null,
    @SerializedName("rating")         val rating: Double? = null,
    @SerializedName("currentRedemptions") val currentRedemptions: Int? = null,
    @SerializedName("status")         val status: String,
    @SerializedName("createdAt")      val createdAt: String,
    @SerializedName("updatedAt")      val updatedAt: String,
    @SerializedName("isActive")       val isActive: Boolean,
)
