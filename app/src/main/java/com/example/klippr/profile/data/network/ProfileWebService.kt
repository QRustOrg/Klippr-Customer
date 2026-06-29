package com.example.klippr.profile.data.network

import com.example.klippr.profile.domain.model.UserResource
import retrofit2.http.GET
import retrofit2.http.Path

// @author Samuel Bonifacio
/** Contrato Retrofit para datos de usuario/perfil. Requiere Bearer (lo añade el AuthInterceptor). */
interface ProfileWebService {

    /** Datos del usuario autenticado (UserResource). El consumer endpoint 404ea; este sí devuelve firstName. */
    @GET("api/Users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserResource
}
