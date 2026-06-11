package com.example.klippr.iam.data.remote.api

import com.example.klippr.iam.data.remote.dto.AuthenticatedUserDto
import com.example.klippr.iam.data.remote.dto.ForgotPasswordRequestDto
import com.example.klippr.iam.data.remote.dto.ResetPasswordRequestDto
import com.example.klippr.iam.data.remote.dto.SignInRequestDto
import com.example.klippr.iam.data.remote.dto.SignUpConsumerRequestDto
import com.example.klippr.iam.data.remote.dto.SignUpConsumerResponseDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

// @author Samuel Bonifacio
/** Contrato Retrofit para IAM. Base URL compartida. */
interface AuthApiService {

    @POST("api/Authentication/sign-in")
    suspend fun signIn(@Body body: SignInRequestDto): AuthenticatedUserDto

    @POST("api/Authentication/sign-up/consumer")
    suspend fun signUpConsumer(@Body body: SignUpConsumerRequestDto): SignUpConsumerResponseDto

    /** Verifica que el email exista (paso 1 del flujo "olvidé mi contraseña"). 404 si no existe. */
    @POST("api/Authentication/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequestDto): ResponseBody

    /** Actualiza la contraseña del usuario identificado por email (paso 2). Reemplazo idempotente → PUT. */
    @PUT("api/Authentication/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequestDto): ResponseBody
}
