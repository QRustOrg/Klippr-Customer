package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.model.User
import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Devuelve el usuario autenticado actual o `null` si no hay sesión. */
class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): User? = repository.currentUser()
}
