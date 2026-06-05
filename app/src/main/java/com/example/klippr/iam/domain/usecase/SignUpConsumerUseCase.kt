package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Registra un nuevo consumidor. El nombre se divide en firstName / lastName en el primer espacio. */
class SignUpConsumerUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Session {
        val parts = name.trim().split(" ", limit = 2)
        val firstName = parts.getOrElse(0) { name.trim() }
        val lastName = parts.getOrElse(1) { "" }
        return repository.signUpConsumer(firstName, lastName, email.trim(), password)
    }
}
