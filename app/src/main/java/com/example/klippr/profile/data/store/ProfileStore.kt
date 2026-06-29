package com.example.klippr.profile.data.store

import com.example.klippr.profile.domain.model.UserProfile

// @author Samuel Bonifacio
/** Contrato de perfil: obtiene los datos del usuario autenticado sin filtrar detalles de red. */
interface ProfileStore {
    /** Datos del usuario actual (resuelve el id desde la sesión guardada). */
    suspend fun getCurrentProfile(): UserProfile
}
