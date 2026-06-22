package com.example.klippr.favorites.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.favorites.domain.usecase.ArchiveFavoriteUseCase
import com.example.klippr.favorites.domain.usecase.GetUserFavoritesUseCase
import com.example.klippr.favorites.domain.usecase.RemoveFavoriteUseCase
import com.example.klippr.favorites.domain.usecase.RestoreFavoriteUseCase
import com.example.klippr.favorites.domain.usecase.SaveFavoriteUseCase
import com.example.klippr.favorites.presentation.state.FavoriteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getUserFavorites: GetUserFavoritesUseCase,
    private val saveFavorite: SaveFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val archiveFavoriteUseCase: ArchiveFavoriteUseCase,
    private val restoreFavoriteUseCase: RestoreFavoriteUseCase,
    ): ViewModel(){

    private val _state = MutableStateFlow(FavoriteUiState())
    val state: StateFlow<FavoriteUiState> = _state.asStateFlow()

    fun loadFavorites(userId: String){
        if (userId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true,error = null)}
            runCatching {
                getUserFavorites(userId, archived = false) to getUserFavorites(userId, archived = true)
            }
                .onSuccess { (active, archived) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            favorites = active,
                            archivedFavorites = archived,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            }
        }

    fun addFavorite(userId: String, promotionId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { saveFavorite(userId, promotionId) }
                .onSuccess { loadFavorites(userId); onDone() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun deleteFavorite(favoriteId: String, userId: String, onDone: () -> Unit = {}){
        viewModelScope.launch {
            runCatching { removeFavorite(favoriteId, userId) }
                .onSuccess { loadFavorites(userId); onDone() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun archiveFavorite(favoriteId: String, userId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { archiveFavoriteUseCase(favoriteId, userId) }
                .onSuccess { loadFavorites(userId); onDone() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun restoreFavorite(favoriteId: String, userId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { restoreFavoriteUseCase(favoriteId, userId) }
                .onSuccess { loadFavorites(userId); onDone() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
