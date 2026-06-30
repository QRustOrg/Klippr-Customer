package com.example.klippr.iam.data.store

import com.example.klippr.iam.data.network.AuthWebService
import com.example.klippr.iam.domain.model.ForgotPasswordRequest
import com.example.klippr.iam.domain.model.ResetPasswordRequest
import com.example.klippr.iam.domain.model.SignInRequest
import com.example.klippr.iam.domain.model.SignUpConsumerRequest
import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import com.example.klippr.shared.data.network.safeApiCall
import com.example.klippr.shared.data.store.SessionDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// @author Samuel Bonifacio
/** Implementa IAM: llama al backend y cachea la sesion en DataStore. */
class AuthStoreImpl(
    private val webService: AuthWebService,
    private val sessionStore: SessionDataStore,
) : AuthStore {

    override suspend fun signIn(email: String, password: String, rememberMe: Boolean): Session {
        val resource = safeApiCall { webService.signIn(SignInRequest(email = email, password = password)) }
        val session = Session(
            token = resource.token,
            user = User(userId = resource.userId, email = resource.email, role = resource.role),
        )
        // Persistimos la sesion para que AuthInterceptor tenga token en los flujos autenticados.
        sessionStore.save(session)
        return session
    }

    override suspend fun signUpConsumer(firstName: String, lastName: String, email: String, password: String): Session {
        // El endpoint de registro crea el usuario pero no devuelve token: tras el 201 hacemos
        // un sign-in automatico para obtener la sesion y persistirla (reutiliza signIn de arriba).
        safeApiCall {
            webService.signUpConsumer(
                SignUpConsumerRequest(email = email, password = password, firstName = firstName, lastName = lastName),
            )
        }
        return signIn(email, password)
    }

    override suspend fun requestPasswordRecovery(email: String) {
        // forgot-password solicita al backend que envie el enlace de recuperacion por correo.
        safeApiCall { webService.forgotPassword(ForgotPasswordRequest(email = email)) }
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        safeApiCall { webService.resetPassword(ResetPasswordRequest(email = email, newPassword = newPassword)) }
    }

    override val session: Flow<Session?> = sessionStore.session

    override suspend fun currentUser(): User? = sessionStore.session.first()?.user

    override suspend fun signOut() = sessionStore.clear()
}
