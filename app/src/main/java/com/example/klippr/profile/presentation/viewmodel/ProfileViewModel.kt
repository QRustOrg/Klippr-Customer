package com.example.klippr.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.profile.application.usecase.GetUserProfileUseCase
import com.example.klippr.profile.presentation.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.klippr.shared.core.ServiceLocator

// @author Samuel Bonifacio
/** Carga y expone los datos del perfil del usuario autenticado (solo lectura). */
class ProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { load() }

    /** (Re)carga el perfil desde el backend. */
    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = getUserProfile()
                _state.update { it.copy(isLoading = false, profile = profile) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "No se pudo cargar el perfil") }
            }
        }
    }

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(
                    getUserProfile = GetUserProfileUseCase(serviceLocator.profileStore),
                ) as T
            }
    }

}