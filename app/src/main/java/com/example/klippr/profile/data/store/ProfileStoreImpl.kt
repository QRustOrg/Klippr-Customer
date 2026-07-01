package com.example.klippr.profile.data.store

import com.example.klippr.profile.data.network.ProfileWebService
import com.example.klippr.profile.domain.model.UserPreference
import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.domain.model.UserResource
import com.example.klippr.profile.domain.model.toRequest
import com.example.klippr.shared.data.network.safeApiCall
import com.example.klippr.shared.data.store.SessionDataStore
import kotlinx.coroutines.flow.first

// @author Samuel Bonifacio
/** Implementa perfil y preferencias para el usuario autenticado. */
class ProfileStoreImpl(
    private val webService: ProfileWebService,
    private val sessionStore: SessionDataStore,
) : ProfileStore {

    override suspend fun getCurrentProfile(): UserProfile {
        val session = currentSession()
        return safeApiCall { webService.getUser(session.user.userId) }.toDomain()
    }

    override suspend fun getCurrentPreference(): UserPreference? {
        val userId = currentSession().user.userId
        return safeApiCall { webService.getPreferences() }
            .firstOrNull { it.userId == userId }
    }

    override suspend fun createPreference(preference: UserPreference): UserPreference =
        safeApiCall { webService.createPreference(preference.toRequest()) }

    override suspend fun updatePreference(preference: UserPreference): UserPreference {
        val id = preference.id ?: throw IllegalStateException("No se encontro la preferencia")
        return safeApiCall { webService.updatePreference(id, preference.toRequest()) }
    }

    private suspend fun currentSession() = sessionStore.session.first()
        ?: throw IllegalStateException("No hay sesion activa")

    private fun UserResource.toDomain() = UserProfile(
        userId = userId,
        email = email.orEmpty(),
        role = role.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        isActive = isActive,
        memberSince = createdAt?.substringBefore("T")?.split("-")
            ?.takeIf { it.size == 3 }
            ?.let { (y, m, d) -> "$d/$m/$y" }
            .orEmpty(),
    )
}
