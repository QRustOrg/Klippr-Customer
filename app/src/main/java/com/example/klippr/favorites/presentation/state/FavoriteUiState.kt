package com.example.klippr.favorites.presentation.state

import com.example.klippr.favorites.domain.model.Favorite

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<Favorite> = emptyList(),
    val error: String? = null,
){
    val isEmpty: Boolean get() = !isLoading && favorites.isEmpty() && error == null
}