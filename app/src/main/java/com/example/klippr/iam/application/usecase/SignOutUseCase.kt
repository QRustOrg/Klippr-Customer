package com.example.klippr.iam.application.usecase

import com.example.klippr.iam.data.store.AuthStore

// @author Samuel Bonifacio
/** Cierra la sesión actual, limpiando el token y la identidad persistidos. */
class SignOutUseCase(private val repository: AuthStore) {
    suspend operator fun invoke() = repository.signOut()
}
