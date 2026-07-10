package com.example.klippr.favorites.presentation.state

import com.example.klippr.favorites.domain.model.Favorite

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<Favorite> = emptyList(),
    val archivedFavorites: List<Favorite> = emptyList(),
    val error: String? = null,
){
    val visibleFavorites: List<Favorite> get() = favorites.filterNot { it.isArchived }
    val isEmpty: Boolean get() = !isLoading && visibleFavorites.isEmpty() && error == null
    val isArchiveEmpty: Boolean get() = !isLoading && archivedFavorites.isEmpty() && error == null
}
