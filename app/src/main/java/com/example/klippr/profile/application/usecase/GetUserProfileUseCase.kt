package com.example.klippr.profile.application.usecase

import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.data.store.ProfileStore

// @author Samuel Bonifacio
/** Obtiene el perfil del usuario autenticado. */
class GetUserProfileUseCase(private val repository: ProfileStore) {
    suspend operator fun invoke(): UserProfile = repository.getCurrentProfile()
}
