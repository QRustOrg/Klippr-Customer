package com.example.klippr.iam.application.usecase

import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.data.store.AuthStore

// @author Samuel Bonifacio
/** Registra un nuevo consumidor con nombre y apellido por separado (como exige el backend). */
class SignUpConsumerUseCase(private val repository: AuthStore) {
    suspend operator fun invoke(firstName: String, lastName: String, email: String, password: String): Session =
        repository.signUpConsumer(firstName.trim(), lastName.trim(), email.trim(), password)
}
