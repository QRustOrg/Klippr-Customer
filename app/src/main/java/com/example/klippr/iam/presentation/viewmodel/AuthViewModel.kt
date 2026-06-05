package com.example.klippr.iam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.iam.domain.usecase.GetCurrentUserUseCase
import com.example.klippr.iam.domain.usecase.SignInUseCase
import com.example.klippr.iam.domain.usecase.SignUpConsumerUseCase
import com.example.klippr.iam.presentation.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @author Samuel Bonifacio
/** Coordina inicio/registro de sesión y restaura la sesión guardada al arrancar. */
class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signUpConsumerUseCase: SignUpConsumerUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init { restoreSession() }

    /** Restaura la sesión persistida (auto-login si el token sigue guardado). */
    private fun restoreSession() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user != null) _state.update { it.copy(user = user) }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Ingresa email y contraseña") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val session = signInUseCase(email, password)
                _state.update { it.copy(isLoading = false, user = session.user) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al iniciar sesión") }
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Completa todos los campos") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val session = signUpConsumerUseCase(name, email, password)
                _state.update { it.copy(isLoading = false, user = session.user) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al registrarse") }
            }
        }
    }

    fun consumeError() = _state.update { it.copy(error = null) }
}
