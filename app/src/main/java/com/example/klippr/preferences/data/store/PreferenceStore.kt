package com.example.klippr.preferences.data.store

import com.example.klippr.preferences.domain.model.UserPreference

// @author Samuel Bonifacio
/** Contrato del bounded context de preferencias del usuario autenticado. */
interface PreferenceStore {
    suspend fun getOrCreateCurrentPreference(): UserPreference

    suspend fun updatePreference(preference: UserPreference): UserPreference
}
