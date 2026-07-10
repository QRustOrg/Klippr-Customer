package com.example.klippr.iam.application.usecase

import com.example.klippr.iam.domain.model.User
import com.example.klippr.iam.data.store.AuthStore

// @author Samuel Bonifacio
/** Devuelve el usuario autenticado actual o `null` si no hay sesión. */
class GetCurrentUserUseCase(private val repository: AuthStore) {
    suspend operator fun invoke(): User? = repository.currentUser()
}
