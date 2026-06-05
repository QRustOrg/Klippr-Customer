package com.example.klippr.iam.domain.repository

import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
/** Contrato de IAM: autentica y expone la sesión persistida sin filtrar detalles de red/almacenamiento. */
interface AuthRepository {
    /** Inicia sesión contra el backend y persiste la sesión resultante. */
    suspend fun signIn(email: String, password: String): Session

    /** Sesión actual reactiva (`null` si no hay usuario). */
    val session: Flow<Session?>

    /** Usuario autenticado actual o `null`. */
    suspend fun currentUser(): User?

    suspend fun signOut()
}
