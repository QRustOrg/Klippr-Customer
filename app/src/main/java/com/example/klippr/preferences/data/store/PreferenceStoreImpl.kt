package com.example.klippr.preferences.data.store

import com.example.klippr.preferences.data.network.PreferenceWebService
import com.example.klippr.preferences.domain.model.UserPreference
import com.example.klippr.preferences.domain.model.toRequest
import com.example.klippr.shared.data.network.safeApiCall
import com.example.klippr.shared.data.store.SessionDataStore
import kotlinx.coroutines.flow.first

// @author Samuel Bonifacio
/** Implementa preferencias para el usuario autenticado contra `/api/v1/Preferences`. */
class PreferenceStoreImpl(
    private val webService: PreferenceWebService,
    private val currentUserIdProvider: suspend () -> String,
) : PreferenceStore {

    constructor(
        webService: PreferenceWebService,
        sessionStore: SessionDataStore,
    ) : this(
        webService = webService,
        currentUserIdProvider = {
            sessionStore.session.first()?.user?.userId
                ?: throw IllegalStateException("No hay sesion activa")
        },
    )

    override suspend fun getOrCreateCurrentPreference(): UserPreference {
        val userId = currentUserIdProvider()
        return safeApiCall { webService.getPreferences() }
            .firstOrNull { it.userId == userId }
            ?: safeApiCall { webService.createPreference(UserPreference.defaults(userId).toRequest()) }
    }

    override suspend fun updatePreference(preference: UserPreference): UserPreference {
        val id = preference.id ?: throw IllegalStateException("No se encontro la preferencia")
        return safeApiCall { webService.updatePreference(id, preference.toRequest()) }
    }
}
