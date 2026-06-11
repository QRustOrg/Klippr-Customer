package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Paso 1 del flujo "olvidé mi contraseña": verifica que el email exista. */
class VerifyEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String) = repository.verifyEmail(email.trim())
}
