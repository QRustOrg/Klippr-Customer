package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Paso 2 del flujo "olvidé mi contraseña": fija la nueva contraseña por email. */
class ResetPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, newPassword: String) =
        repository.resetPassword(email.trim(), newPassword)
}
