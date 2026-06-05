package com.example.klippr.iam.data.repository

import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.iam.data.remote.api.AuthApiService
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

    override suspend fun signIn(email: String, password: String): Session {
        val dto = api.signIn(SignInRequestDto(email = email, password = password))
        val session = Session(
            token = dto.token,
            user = User(userId = dto.userId, email = dto.email, role = dto.role),
        )
        sessionStore.save(session)
        return session
    }

    override suspend fun signUpConsumer(firstName: String, lastName: String, email: String, password: String): Session {
        val dto = api.signUpConsumer(SignUpConsumerRequestDto(email = email, password = password, firstName = firstName, lastName = lastName))
        val session = Session(token = dto.token, user = User(userId = dto.userId, email = dto.email, role = dto.role))
        sessionStore.save(session)
        return session
    }

    override val session: Flow<Session?> = sessionStore.session

    override suspend fun currentUser(): User? = sessionStore.session.first()?.user

    override suspend fun signOut() = sessionStore.clear()
}
