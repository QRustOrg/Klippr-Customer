package com.example.klippr.iam.presentation.state

import com.example.klippr.iam.domain.model.User

// @author Samuel Bonifacio
/** Estado de la pantalla de autenticación. */
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    // Flujo "olvidé mi contraseña":
    val forgotEmail: String? = null,   // email usado para solicitar recuperacion
    val passwordRecoverySent: Boolean = false, // confirma que el backend acepto enviar el enlace
    val resetSuccess: Boolean = false,  // gatilla volver a SignIn tras cambiar la contraseña
) {
    val isAuthenticated: Boolean get() = user != null
}
