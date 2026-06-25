package com.example.klippr.favorites.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.favorites.domain.usecase.ArchiveFavoriteUseCase
import com.example.klippr.favorites.domain.usecase.GetFavoriteByIdUseCase
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
    private val getFavoriteById: GetFavoriteByIdUseCase,
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
            refreshFavorites(userId)
        }
    }

    fun addFavorite(userId: String, promotionId: String, onDone: () -> Unit = {}) {
        if (userId.isBlank() || promotionId.isBlank()) return
        viewModelScope.launch {
            runFavoriteOperation(
                userId = userId,
                operation = { saveFavorite(userId, promotionId) },
                onDone = onDone,
            )
        }
    }

    fun deleteFavorite(favoriteId: String, userId: String, onDone: () -> Unit = {}){
        if (favoriteId.isBlank() || userId.isBlank()) return
        viewModelScope.launch {
            runFavoriteOperation(
                userId = userId,
                operation = { removeFavorite(favoriteId, userId) },
                onDone = onDone,
            )
        }
    }

    fun archiveFavorite(favoriteId: String, userId: String, onDone: () -> Unit = {}) {
        if (favoriteId.isBlank() || userId.isBlank()) return
        viewModelScope.launch {
            runFavoriteOperation(
                userId = userId,
                operation = { archiveFavoriteUseCase(favoriteId, userId) },
                onDone = onDone,
            )
        }
    }

    fun restoreFavorite(favoriteId: String, userId: String, onDone: () -> Unit = {}) {
        if (favoriteId.isBlank() || userId.isBlank()) return
        viewModelScope.launch {
            runFavoriteOperation(
                userId = userId,
                operation = { restoreFavoriteUseCase(favoriteId, userId) },
                onDone = onDone,
            )
        }
    }

    private suspend fun runFavoriteOperation(
        userId: String,
        operation: suspend () -> Unit,
        onDone: () -> Unit,
    ) {
        val result = runCatching { operation() }
        refreshFavorites(userId)
        result
            .onSuccess { onDone() }
            .onFailure { e -> _state.update { it.copy(error = e.message) } }
    }

    private suspend fun refreshFavorites(userId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
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

    fun openFavoriteDetails(favoriteId: String, onResolvedPromotionId: (String) -> Unit) {
        if (favoriteId.isBlank()) return
        viewModelScope.launch {
            runCatching { getFavoriteById(favoriteId) }
                .onSuccess { favorite -> onResolvedPromotionId(favorite.promotionId) }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
