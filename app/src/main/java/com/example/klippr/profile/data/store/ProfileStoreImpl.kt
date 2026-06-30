package com.example.klippr.profile.data.store

import com.example.klippr.profile.data.network.ProfileWebService
import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.domain.model.UserResource
import com.example.klippr.shared.data.network.safeApiCall
import com.example.klippr.shared.data.store.SessionDataStore
import kotlinx.coroutines.flow.first

// @author Samuel Bonifacio
/** Implementa el perfil: resuelve el userId de la sesion y consulta `GET /api/Users/{userId}`. */
class ProfileStoreImpl(
    private val webService: ProfileWebService,
    private val sessionStore: SessionDataStore,
) : ProfileStore {

    override suspend fun getCurrentProfile(): UserProfile {
        val session = sessionStore.session.first()
            ?: throw IllegalStateException("No hay sesion activa")
        return safeApiCall { webService.getUser(session.user.userId) }.toDomain()
    }

    private fun UserResource.toDomain() = UserProfile(
        userId = userId,
        email = email.orEmpty(),
        role = role.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        isActive = isActive,
        // El backend envia ISO-8601 ("2026-06-11T01:12:28..."); mostramos solo la fecha.
        memberSince = createdAt?.substringBefore("T")?.split("-")
            ?.takeIf { it.size == 3 }
            ?.let { (y, m, d) -> "$d/$m/$y" }
            .orEmpty(),
    )
}
