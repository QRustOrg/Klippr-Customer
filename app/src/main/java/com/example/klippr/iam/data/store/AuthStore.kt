package com.example.klippr.iam.data.store

import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
/** Contrato de IAM: autentica y expone la sesión persistida sin filtrar detalles de red/almacenamiento. */
interface AuthStore {
    /** Inicia sesión contra el backend. Persiste la sesión en disco solo si [rememberMe] es true. */
    suspend fun signIn(email: String, password: String, rememberMe: Boolean = true): Session

    /** Registra nuevo consumidor y persiste la sesión resultante. */
    suspend fun signUpConsumer(firstName: String, lastName: String, email: String, password: String): Session

    /** Solicita al backend el envio del enlace de recuperacion para el email indicado. */
    suspend fun requestPasswordRecovery(email: String)

    /** Fija una nueva contraseña para el usuario identificado por email. Lanza [ApiException] en error. */
    suspend fun resetPassword(email: String, newPassword: String)

    /** Sesión actual reactiva (`null` si no hay usuario). */
    val session: Flow<Session?>

    /** Usuario autenticado actual o `null`. */
    suspend fun currentUser(): User?

    suspend fun signOut()
}
