package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Inicia sesión con email y contraseña. */
class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Session =
        repository.signIn(email.trim(), password)
}
