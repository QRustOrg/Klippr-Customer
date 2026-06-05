package com.example.klippr.iam.data.remote.api

import com.example.klippr.iam.data.remote.dto.AuthenticatedUserDto
import com.example.klippr.iam.data.remote.dto.SignInRequestDto
import com.example.klippr.iam.data.remote.dto.SignUpConsumerRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

// @author Samuel Bonifacio
/** Contrato Retrofit para IAM. Base URL compartida. */
interface AuthApiService {

    @POST("api/Authentication/sign-in")
    suspend fun signIn(@Body body: SignInRequestDto): AuthenticatedUserDto

    @POST("api/Authentication/sign-up/consumer")
    suspend fun signUpConsumer(@Body body: SignUpConsumerRequestDto): AuthenticatedUserDto
}
