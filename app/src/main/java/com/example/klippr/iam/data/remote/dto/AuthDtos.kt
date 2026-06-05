package com.example.klippr.iam.data.remote.dto

import com.google.gson.annotations.SerializedName

// @author Samuel Bonifacio
/** Cuerpo de POST /api/Authentication/sign-in. */
data class SignInRequestDto(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String,
)

/** Respuesta de sign-in / sign-up: identidad + token de acceso. */
data class AuthenticatedUserDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("email")  val email: String,
    @SerializedName("role")   val role: String,
    @SerializedName("token")  val token: String,
)
