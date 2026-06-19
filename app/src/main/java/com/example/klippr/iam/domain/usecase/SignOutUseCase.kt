package com.example.klippr.iam.domain.usecase

import com.example.klippr.iam.domain.repository.AuthRepository

// @author Samuel Bonifacio
/** Cierra la sesión actual, limpiando el token y la identidad persistidos. */
class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}
