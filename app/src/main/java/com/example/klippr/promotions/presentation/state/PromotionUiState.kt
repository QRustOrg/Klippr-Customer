package com.example.klippr.promotions.presentation.state

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory

// @author Samuel Bonifacio
// Estados de UI para listado y detalle. `displayed` aplica filtro de categoría en memoria.
data class PromotionListState(
    val isLoading: Boolean = false,
    val promotions: List<Promotion> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: PromotionCategory? = null,
    val error: String? = null,
) {
    val isEmpty: Boolean get() = !isLoading && promotions.isEmpty() && error == null
    val displayed: List<Promotion> get() = selectedCategory
        ?.let { cat -> promotions.filter { it.category == cat } }
        ?: promotions
}

data class PromotionDetailState(
    val isLoading: Boolean = false,
    val promotion: Promotion? = null,
    val error: String? = null,
)
