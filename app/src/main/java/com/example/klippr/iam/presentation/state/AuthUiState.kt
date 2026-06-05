package com.example.klippr.iam.presentation.state

import com.example.klippr.iam.domain.model.User

// @author Samuel Bonifacio
/** Estado de la pantalla de autenticación. */
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
) {
    val isAuthenticated: Boolean get() = user != null
}
