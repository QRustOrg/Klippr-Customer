package com.example.klippr.profile.data.store

import com.example.klippr.profile.domain.model.UserPreference
import com.example.klippr.profile.domain.model.UserProfile

// @author Samuel Bonifacio
/** Contrato de perfil: usuario autenticado y preferencias de cuenta. */
interface ProfileStore {
    suspend fun getCurrentProfile(): UserProfile

    suspend fun getCurrentPreference(): UserPreference?

    suspend fun createPreference(preference: UserPreference): UserPreference

    suspend fun updatePreference(preference: UserPreference): UserPreference
}
