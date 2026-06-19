package com.example.klippr.core.network

import com.example.klippr.core.datastore.SessionDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// @author Samuel Bonifacio
/**
 * Añade `Authorization: Bearer <token>` a cada petición cuando hay sesión.
 * Lee el token de forma sincrónica desde DataStore (la llamada ya corre en un hilo de red).
 */
class AuthInterceptor(private val sessionStore: SessionDataStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionStore.currentToken() }
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
