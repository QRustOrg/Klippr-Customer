package com.example.klippr.iam.domain.model

import com.google.gson.annotations.SerializedName

// @author Samuel Bonifacio
/** Cuerpo de POST /api/Authentication/sign-in. */
data class SignInRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String,
)

/** Cuerpo de POST /api/Authentication/forgot-password: verifica que el email exista. */
data class ForgotPasswordRequest(
    @SerializedName("email") val email: String,
)

/** Cuerpo de PUT /api/Authentication/reset-password: fija la nueva contraseña por email. */
data class ResetPasswordRequest(
    @SerializedName("email")       val email: String,
    @SerializedName("newPassword") val newPassword: String,
)

/** Cuerpo de POST /api/Authentication/sign-up/consumer. */
data class SignUpConsumerRequest(
    @SerializedName("email")     val email: String,
    @SerializedName("password")  val password: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName")  val lastName: String,
)

/** Respuesta de sign-in: identidad + token de acceso (`AuthenticatedUserResource`). */
data class AuthenticatedUserResource(
    @SerializedName("userId") val userId: String,
    @SerializedName("email")  val email: String,
    @SerializedName("role")   val role: String,
    @SerializedName("token")  val token: String,
)

/**
 * Respuesta de sign-up/consumer (`UserResource`, 201): el backend crea el usuario pero
 * **no devuelve token**. Por eso, tras registrarse, el repositorio hace un sign-in automático
 * para obtener la sesión. (Gson ignora los campos extra del recurso que no mapeamos.)
 */
data class SignUpConsumerResource(
    @SerializedName("userId")    val userId: String,
    @SerializedName("email")     val email: String,
    @SerializedName("role")      val role: String,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName")  val lastName: String? = null,
)
