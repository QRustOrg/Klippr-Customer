package com.example.klippr.shared.data.network

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

// @author Samuel Bonifacio
/**
 * Error de dominio para la capa de red: su `message` ya viene en formato legible para el usuario
 * (sea el `message` que devolvió el backend o un texto por defecto). El `AuthViewModel` lo muestra
 * tal cual en pantalla.
 */
class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

const val SESSION_EXPIRED_MESSAGE = "Tu sesión expiró. Inicia sesión nuevamente."

/** Cuerpo de error estándar del backend: `{ "message": "..." }`. */
private data class ErrorResponse(
    val message: String?,
    val detail: String?,
    val title: String?,
)

private val gson = Gson()

/**
 * Envuelve una llamada Retrofit y traduce sus fallos a [ApiException] con un mensaje legible:
 * - `HttpException` (4xx/5xx) → parsea `{ "message": "..." }` del `errorBody`; si no, "Error <code>".
 * - `IOException` (sin red) → mensaje de conexión.
 * Reutilizable por cualquier repositorio que use el cliente Retrofit compartido (ApiClient).
 */
suspend fun <T> safeApiCall(block: suspend () -> T): T =
    try {
        block()
    } catch (e: HttpException) {
        if (e.code() == 401) {
            throw ApiException(SESSION_EXPIRED_MESSAGE, e)
        }
        val backendMessage = e.response()?.errorBody()?.string()
            ?.let {
                runCatching {
                    gson.fromJson(it, ErrorResponse::class.java)
                        ?.let { dto -> dto.message ?: dto.detail ?: dto.title }
                }.getOrNull()
            }
            ?.takeIf { it.isNotBlank() }
        throw ApiException(backendMessage ?: "Error ${e.code()}", e)
    } catch (e: IOException) {
        throw ApiException("Sin conexión. Verifica tu internet.", e)
    }
