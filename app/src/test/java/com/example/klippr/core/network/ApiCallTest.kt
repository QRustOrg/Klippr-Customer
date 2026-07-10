package com.example.klippr.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ApiCallTest {

    @Test
    fun safeApiCall_mapsUnauthorizedToSessionExpiredMessage() = runBlocking {
        val result = runCatching {
            safeApiCall<Unit> {
                throw HttpException(Response.error<Unit>(401, "".toResponseBody()))
            }
        }

        val exception = result.exceptionOrNull()
        assertTrue(exception is ApiException)
        assertEquals("Tu sesión expiró. Inicia sesión nuevamente.", exception?.message)
    }
}
