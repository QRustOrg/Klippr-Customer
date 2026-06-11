package com.example.klippr.profile.data.remote.api

import com.example.klippr.profile.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para datos de usuario/perfil. Requiere Bearer (lo añade el AuthInterceptor). */
interface ProfileApiService {

    /** Datos del usuario actual por id (el `userId` guardado en sesión al iniciar sesión). */
    @GET("api/Users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserDto
}
