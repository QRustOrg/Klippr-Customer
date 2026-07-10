package com.example.klippr.promotions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import com.example.klippr.promotions.data.store.PromotionStore
import com.example.klippr.promotions.application.usecase.GetActivePromotionsUseCase
import com.example.klippr.promotions.application.usecase.GetAllPromotionsUseCase
import com.example.klippr.promotions.application.usecase.GetPromotionByIdUseCase
import com.example.klippr.promotions.application.usecase.SearchPromotionsUseCase
import com.example.klippr.promotions.application.usecase.ToggleFavoriteUseCase
import com.example.klippr.promotions.presentation.state.PromotionDetailState
import com.example.klippr.promotions.presentation.state.PromotionListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.klippr.shared.core.ServiceLocator

// @author Samuel Bonifacio
// ViewModel único para lista y detalle. Las colecciones de Flow se lanzan en viewModelScope.
class PromotionViewModel(
    private val getAllPromotions: GetAllPromotionsUseCase,
    private val getActivePromotions: GetActivePromotionsUseCase,
    private val getPromotionById: GetPromotionByIdUseCase,
    private val searchPromotions: SearchPromotionsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val repository: PromotionStore,
) : ViewModel() {

    private val _listState = MutableStateFlow(PromotionListState())
    val listState: StateFlow<PromotionListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PromotionDetailState())
    val detailState: StateFlow<PromotionDetailState> = _detailState.asStateFlow()

    // Cache id_negocio -> nombre, resuelto on-demand al abrir el modal de una promo.
    private val _businessNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val businessNames: StateFlow<Map<String, String>> = _businessNames.asStateFlow()

    fun loadBusinessName(businessId: String) {
        if (businessId.isBlank() || _businessNames.value.containsKey(businessId)) return
        viewModelScope.launch {
            repository.getBusinessName(businessId)?.let { name ->
                _businessNames.update { it + (businessId to name) }
            }
        }
    }

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

    fun onActiveSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadActive()
            return
        }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            searchPromotions(query)
                .catch { e -> _listState.update { it.copy(error = e.message) } }
                .collect { list ->
                    _listState.update {
                        it.copy(promotions = list.filter { promo -> promo.status == PromotionStatus.PUBLISHED })
                    }
                }
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

    companion object {
        fun Factory(serviceLocator: ServiceLocator): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = PromotionViewModel(
                    getAllPromotions = GetAllPromotionsUseCase(serviceLocator.promotionStore),
                    getActivePromotions = GetActivePromotionsUseCase(serviceLocator.promotionStore),
                    getPromotionById = GetPromotionByIdUseCase(serviceLocator.promotionStore),
                    searchPromotions = SearchPromotionsUseCase(serviceLocator.promotionStore),
                    toggleFavoriteUseCase = ToggleFavoriteUseCase(serviceLocator.promotionStore),
                    repository = serviceLocator.promotionStore,
                ) as T
            }
    }

}