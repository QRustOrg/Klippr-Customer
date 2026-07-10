package com.example.klippr.iam.application.usecase

import com.example.klippr.iam.data.store.AuthStore

// @author Samuel Bonifacio
/** Paso 2 del flujo "olvidé mi contraseña": fija la nueva contraseña por email. */
class ResetPasswordUseCase(private val repository: AuthStore) {
    suspend operator fun invoke(email: String, newPassword: String) =
        repository.resetPassword(email.trim(), newPassword)
}
