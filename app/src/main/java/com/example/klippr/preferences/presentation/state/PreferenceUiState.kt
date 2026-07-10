package com.example.klippr.preferences.presentation.state

import com.example.klippr.preferences.domain.model.UserPreference

// @author Samuel Bonifacio
/** Estado de preferencias de cuenta en Settings. */
data class PreferenceUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val preference: UserPreference? = null,
    val error: String? = null,
    val saveMessage: String? = null,
)
