package com.example.klippr.iam.application.usecase

import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.data.store.AuthStore

// @author Samuel Bonifacio
/** Inicia sesión con email y contraseña. */
class SignInUseCase(private val repository: AuthStore) {
    suspend operator fun invoke(email: String, password: String, rememberMe: Boolean = true): Session =
        repository.signIn(email.trim(), password, rememberMe)
}
