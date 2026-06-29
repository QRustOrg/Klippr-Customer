package com.example.klippr.profile.domain.model

import com.google.gson.annotations.SerializedName

// @author Samuel Bonifacio
/**
 * Respuesta de `GET /api/Users/{userId}` (`UserResource`). Identidad y datos básicos del usuario.
 * Para consumidores, `businessName`/`taxId` llegan en `null`.
 */
data class UserResource(
    @SerializedName("userId")       val userId: String,
    @SerializedName("email")        val email: String?,
    @SerializedName("role")         val role: String?,
    @SerializedName("firstName")    val firstName: String?,
    @SerializedName("lastName")     val lastName: String?,
    @SerializedName("businessName") val businessName: String?,
    @SerializedName("taxId")        val taxId: String?,
    @SerializedName("isActive")     val isActive: Boolean = true,
    @SerializedName("createdAt")    val createdAt: String?,
    @SerializedName("updatedAt")    val updatedAt: String?,
)
