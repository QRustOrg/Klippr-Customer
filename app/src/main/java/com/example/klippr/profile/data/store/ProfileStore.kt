package com.example.klippr.profile.data.store

import com.example.klippr.profile.domain.model.UserProfile

// @author Samuel Bonifacio
/** Contrato de perfil del usuario autenticado. */
interface ProfileStore {
    suspend fun getCurrentProfile(): UserProfile
}
