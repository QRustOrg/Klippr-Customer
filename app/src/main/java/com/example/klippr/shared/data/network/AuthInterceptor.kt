package com.example.klippr.shared.data.network

import com.example.klippr.shared.data.store.SessionDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// @author Samuel Bonifacio
/**
 * Anade `Authorization: Bearer <token>` a cada peticion cuando hay sesion.
 * Lee el token de forma sincronica desde DataStore (la llamada ya corre en un hilo de red).
 * En un 401 expira la sesion para devolver al usuario al login.
 */
class AuthInterceptor(private val sessionStore: SessionDataStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val isAuthEndpoint = originalRequest.url.encodedPath.startsWith("/api/Authentication/")
        val token = runBlocking { sessionStore.currentToken() }
        val request = if (!isAuthEndpoint && !token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        val response = chain.proceed(request)
        if (!isAuthEndpoint && !token.isNullOrBlank() && response.code == 401) {
            runBlocking { sessionStore.expireSession() }
        }
        return response
    }
}
