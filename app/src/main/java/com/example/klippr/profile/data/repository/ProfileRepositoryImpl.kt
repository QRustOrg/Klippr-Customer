package com.example.klippr.profile.data.repository

import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.core.network.safeApiCall
import com.example.klippr.profile.data.remote.api.ProfileApiService
import com.example.klippr.profile.data.remote.dto.UserDto
import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first

// @author Samuel Bonifacio
/** Implementa el perfil: resuelve el userId de la sesión y consulta `GET /api/Users/{userId}`. */
class ProfileRepositoryImpl(
    private val api: ProfileApiService,
    private val sessionStore: SessionDataStore,
) : ProfileRepository {

    override suspend fun getCurrentProfile(): UserProfile {
        val session = sessionStore.session.first()
            ?: throw IllegalStateException("No hay sesión activa")
        val dto = safeApiCall { api.getUser(session.user.userId) }
        return dto.toDomain()
    }

    private fun UserDto.toDomain() = UserProfile(
        userId = userId,
        email = email.orEmpty(),
        role = role.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        isActive = isActive,
        // El backend envía ISO-8601 ("2026-06-11T01:12:28..."); mostramos solo la fecha.
        memberSince = createdAt?.substringBefore("T")?.split("-")
            ?.takeIf { it.size == 3 }
            ?.let { (y, m, d) -> "$d/$m/$y" }
            .orEmpty(),
    )
}
