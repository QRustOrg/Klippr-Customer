package com.example.klippr.iam.data.network

import com.example.klippr.iam.domain.model.AuthenticatedUserResource
import com.example.klippr.iam.domain.model.ForgotPasswordRequest
import com.example.klippr.iam.domain.model.ResetPasswordRequest
import com.example.klippr.iam.domain.model.SignInRequest
import com.example.klippr.iam.domain.model.SignUpConsumerRequest
import com.example.klippr.iam.domain.model.SignUpConsumerResource
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

// @author Samuel Bonifacio
/** Contrato Retrofit para IAM. Base URL compartida. */
interface AuthWebService {

    @POST("api/Authentication/sign-in")
    suspend fun signIn(@Body body: SignInRequest): AuthenticatedUserResource

    @POST("api/Authentication/sign-up/consumer")
    suspend fun signUpConsumer(@Body body: SignUpConsumerRequest): SignUpConsumerResource

    /** Verifica que el email exista (paso 1 del flujo "olvidé mi contraseña"). 404 si no existe. */
    @POST("api/Authentication/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): ResponseBody

    /** Actualiza la contraseña del usuario identificado por email (paso 2). Reemplazo idempotente → PUT. */
    @PUT("api/Authentication/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): ResponseBody
}
