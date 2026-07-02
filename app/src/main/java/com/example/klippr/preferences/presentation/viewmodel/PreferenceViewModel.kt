package com.example.klippr.preferences.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.klippr.preferences.data.store.PreferenceStore
import com.example.klippr.preferences.domain.model.UserPreference
import com.example.klippr.preferences.presentation.state.PreferenceUiState
import com.example.klippr.shared.core.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @author Samuel Bonifacio
/** Carga y guarda preferencias de cuenta para Settings. */
class PreferenceViewModel(
    private val preferenceStore: PreferenceStore,
) : ViewModel() {

    private val _state = MutableStateFlow(PreferenceUiState())
    val state: StateFlow<PreferenceUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, saveMessage = null) }
            try {
                val preference = preferenceStore.getOrCreateCurrentPreference()
                _state.update {
                    it.copy(isLoading = false, preference = preference, error = null)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "No se pudieron cargar las preferencias")
                }
            }
        }
    }

    fun savePreference(preference: UserPreference) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, saveMessage = null) }
            try {
                val saved = preferenceStore.updatePreference(preference)
                _state.update {
                    it.copy(
                        isSaving = false,
                        preference = saved,
                        error = null,
                        saveMessage = "Preferencias guardadas",
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "No se pudieron guardar las preferencias",
                    )
                }
            }
        }
    }

    fun consumeSaveMessage() {
        _state.update { it.copy(saveMessage = null) }
    }

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = PreferenceViewModel(
                    preferenceStore = serviceLocator.preferenceStore,
                ) as T
            }
    }
}
