package com.example.klippr.iam.data.repository

import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.core.network.safeApiCall
import com.example.klippr.iam.data.remote.api.AuthApiService
import com.example.klippr.iam.data.remote.dto.ForgotPasswordRequestDto
import com.example.klippr.iam.data.remote.dto.ResetPasswordRequestDto
import com.example.klippr.iam.data.remote.dto.SignInRequestDto
import com.example.klippr.iam.data.remote.dto.SignUpConsumerRequestDto
import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import com.example.klippr.iam.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// @author Samuel Bonifacio
/** Implementa IAM: llama al backend y cachea la sesión en DataStore. */
class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val sessionStore: SessionDataStore,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String, rememberMe: Boolean): Session {
        val dto = safeApiCall { api.signIn(SignInRequestDto(email = email, password = password)) }
        val session = Session(
            token = dto.token,
            user = User(userId = dto.userId, email = dto.email, role = dto.role),
        )
        // Persistimos la sesion para que AuthInterceptor tenga token en los flujos autenticados.
        sessionStore.save(session)
        return session
    }

    override suspend fun signUpConsumer(firstName: String, lastName: String, email: String, password: String): Session {
        // El endpoint de registro crea el usuario pero no devuelve token: tras el 201 hacemos
        // un sign-in automático para obtener la sesión y persistirla (reutiliza signIn de arriba).
        safeApiCall {
            api.signUpConsumer(
                SignUpConsumerRequestDto(email = email, password = password, firstName = firstName, lastName = lastName),
            )
        }
        return signIn(email, password)
    }

    override suspend fun verifyEmail(email: String) {
        // forgot-password devuelve 2xx si el email existe; safeApiCall traduce el 404 a ApiException.
        safeApiCall { api.forgotPassword(ForgotPasswordRequestDto(email = email)) }
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        safeApiCall { api.resetPassword(ResetPasswordRequestDto(email = email, newPassword = newPassword)) }
    }

    override val session: Flow<Session?> = sessionStore.session

    override suspend fun currentUser(): User? = sessionStore.session.first()?.user

    override suspend fun signOut() = sessionStore.clear()
}
