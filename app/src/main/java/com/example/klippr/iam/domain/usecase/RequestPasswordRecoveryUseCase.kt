package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Solicita el enlace de recuperacion de contrasena por correo. */
class RequestPasswordRecoveryUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String) = repository.requestPasswordRecovery(email.trim())
}
