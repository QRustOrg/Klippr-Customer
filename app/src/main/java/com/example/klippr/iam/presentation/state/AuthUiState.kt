package com.example.klippr.iam.presentation.state

import com.example.klippr.iam.domain.model.User

// @author Samuel Bonifacio
/** Estado de la pantalla de autenticación. */
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    // Flujo "olvidé mi contraseña":
    val forgotEmail: String? = null,   // email validado, se conserva entre las dos pantallas
    val emailVerified: Boolean = false, // gatilla navegación a la pantalla de reset
    val resetSuccess: Boolean = false,  // gatilla volver a SignIn tras cambiar la contraseña
) {
    val isAuthenticated: Boolean get() = user != null
}
