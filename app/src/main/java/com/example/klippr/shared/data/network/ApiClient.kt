package com.example.klippr.shared.data.network

import com.example.klippr.shared.data.store.SessionDataStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// @author Samuel Bonifacio
/**
 * Punto unico de configuracion de Retrofit (reemplaza al antiguo NetworkModule).
 *
 * Es un `object` para que exista una sola instancia de [Retrofit]/[OkHttpClient] por proceso.
 * El [ServiceLocator] llama a [install] con el [SessionDataStore] al arrancar, antes de crear
 * cualquier WebService; el [AuthInterceptor] lee el token vivo en cada peticion.
 */
object ApiClient {

    private const val BASE_URL = "https://klippr-backend-production.up.railway.app/"

    private lateinit var sessionStore: SessionDataStore

    /** Cablea la fuente de sesion (token + manejo de 401). Idempotente. */
    fun install(sessionStore: SessionDataStore) {
        this.sessionStore = sessionStore
    }

    val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionStore))
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Crea un WebService Retrofit del tipo [T]. */
    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}
