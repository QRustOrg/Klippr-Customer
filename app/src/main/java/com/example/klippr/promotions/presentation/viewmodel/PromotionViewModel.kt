package com.example.klippr.promotions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.repository.PromotionRepository
import com.example.klippr.promotions.domain.usecase.GetActivePromotionsUseCase
import com.example.klippr.promotions.domain.usecase.GetAllPromotionsUseCase
import com.example.klippr.promotions.domain.usecase.GetPromotionByIdUseCase
import com.example.klippr.promotions.domain.usecase.SearchPromotionsUseCase
import com.example.klippr.promotions.domain.usecase.ToggleFavoriteUseCase
import com.example.klippr.promotions.presentation.state.PromotionDetailState
import com.example.klippr.promotions.presentation.state.PromotionListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @author Samuel Bonifacio
// ViewModel único para lista y detalle. Las colecciones de Flow se lanzan en viewModelScope.
class PromotionViewModel(
    private val getAllPromotions: GetAllPromotionsUseCase,
    private val getActivePromotions: GetActivePromotionsUseCase,
    private val getPromotionById: GetPromotionByIdUseCase,
    private val searchPromotions: SearchPromotionsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val repository: PromotionRepository,
) : ViewModel() {

    private val _listState = MutableStateFlow(PromotionListState())
    val listState: StateFlow<PromotionListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PromotionDetailState())
    val detailState: StateFlow<PromotionDetailState> = _detailState.asStateFlow()

    // Colector activo de la lista. Se cancela antes de iniciar otro para que loadAll/loadActive/
    // search/favorites no compitan escribiendo `promotions` al mismo tiempo.
    private var observeJob: Job? = null

    init { loadAll() }

    fun loadAll() {
        // Refresca desde la red (GET /api/promotions) → Room.
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.refreshAll() }
                .onFailure { e -> _listState.update { it.copy(error = e.message) } }
        }
        // Observa la caché de Room → state (emite inicial y tras el upsert).
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            getAllPromotions()
                .catch { e -> _listState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _listState.update { it.copy(isLoading = false, promotions = list) } }
        }
    }

    fun loadActive() {
        // Refresca solo promociones activas desde la red (GET /api/promotions/active) → Room.
        // Lo consume la pantalla de exploración para mostrar lo que está vigente en la app Flutter.
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.refreshActive() }
                .onFailure { e -> _listState.update { it.copy(error = e.message) } }
        }
        // Observa la caché filtrada por estado PUBLISHED → state.
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            getActivePromotions()
                .catch { e -> _listState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _listState.update { it.copy(isLoading = false, promotions = list) } }
        }
    }

    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadAll()
            return
        }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            searchPromotions(query)
                .catch { e -> _listState.update { it.copy(error = e.message) } }
                .collect { list -> _listState.update { it.copy(promotions = list) } }
        }
    }

    fun onCategorySelected(category: PromotionCategory?) {
        _listState.update { it.copy(selectedCategory = category) }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val promo = getPromotionById(id)
            if (promo != null) {
                _detailState.update { it.copy(isLoading = false, promotion = promo) }
            } else {
                _detailState.update { it.copy(isLoading = false, error = "Promoción no encontrada") }
            }
        }
    }

    fun toggleFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch { toggleFavoriteUseCase(id, isFavorite) }
    }

    fun loadFavorites() {
        _listState.update { it.copy(isLoading = true, error = null) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.getFavorites()
                .catch { e -> _listState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _listState.update { it.copy(isLoading = false, promotions = list) } }
        }
    }
}
