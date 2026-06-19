package com.example.klippr.profile.domain.usecase

import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.domain.repository.ProfileRepository

// @author Samuel Bonifacio
/** Obtiene el perfil del usuario autenticado. */
class GetUserProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): UserProfile = repository.getCurrentProfile()
}
