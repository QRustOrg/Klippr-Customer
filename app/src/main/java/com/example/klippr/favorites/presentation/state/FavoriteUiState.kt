package com.example.klippr.favorites.presentation.state

import com.example.klippr.favorites.domain.model.Favorite

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<Favorite> = emptyList(),
    // ponytail: archivado solo en memoria (no hay campo de archivado en backend); se pierde al matar el proceso.
    // upgrade path: persistir en Room o agregar campo "archived" en el API de Favorites.
    val archivedIds: Set<String> = emptySet(),
    val error: String? = null,
){
    val visibleFavorites: List<Favorite> get() = favorites.filterNot { archivedIds.contains(it.favoriteId) }
    val isEmpty: Boolean get() = !isLoading && visibleFavorites.isEmpty() && error == null
}
