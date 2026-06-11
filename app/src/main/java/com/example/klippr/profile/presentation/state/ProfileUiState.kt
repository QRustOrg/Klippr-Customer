package com.example.klippr.profile.presentation.state

import com.example.klippr.profile.domain.model.UserProfile

// @author Samuel Bonifacio
/** Estado de la pantalla de perfil. */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
)
