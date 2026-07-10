package com.example.klippr.iam.application.usecase

import com.example.klippr.iam.data.store.AuthStore

// @author Samuel Bonifacio
/** Solicita el enlace de recuperacion de contrasena por correo. */
class RequestPasswordRecoveryUseCase(private val repository: AuthStore) {
    suspend operator fun invoke(email: String) = repository.requestPasswordRecovery(email.trim())
}
