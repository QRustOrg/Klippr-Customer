package com.example.klippr.promotions.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel

// @author Samuel Bonifacio

private val KlipprPurple = Color(0xFF887BF3)
private val KlipprLavender = Color(0xFFF0D8FF)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF888888)

/** Estado local de los filtros activos en la pantalla de exploración (US-02). */
private data class ExploreFilterState(
    val discountType: DiscountType? = null,
    val availableOnly: Boolean = false,
    val sortByPopularity: Boolean = false,
    val hasLocationOnly: Boolean = false,
) {
    val isActive: Boolean
        get() = discountType != null || availableOnly || sortByPopularity || hasLocationOnly
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: PromotionViewModel,
    onPromotionClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterPanel by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(ExploreFilterState()) }

    // Aplica los filtros activos a la lista de promociones.
    val filteredPromos = remember(state.promotions, filterState) {
        var result = state.promotions
        filterState.discountType?.let { dt -> result = result.filter { it.discountType == dt } }
        if (filterState.availableOnly) result = result.filter { it.availableRedemptions > it.currentRedemptions }
        if (filterState.sortByPopularity) result = result.sortedByDescending { it.rating ?: 0.0 }
        if (filterState.hasLocationOnly) result = result.filter { it.locationName != null }
        result
    }

    val groupedPromos = remember(filteredPromos) { filteredPromos.groupBy { it.category } }
    val sections = remember(groupedPromos) {
        PromotionCategory.entries.filter { groupedPromos.containsKey(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Promos", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KlipprPurple),
            )
        },
        bottomBar = { ExploreBottomBar() },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    ExploreSearchRow(
                        query = searchQuery,
                        isFilterActive = filterState.isActive,
                        onQueryChange = { searchQuery = it; viewModel.onSearchQueryChange(it) },
                        onFilterClick = { showFilterPanel = !showFilterPanel },
                    )
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(240.dp),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator(color = KlipprPurple) }
                    }
                } else if (state.error != null) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("Error al cargar", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = viewModel::loadActive) {
                                Text("Reintentar", color = KlipprPurple)
                            }
                        }
                    }
                } else if (filteredPromos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("😕", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = if (filterState.isActive) "Sin resultados con estos filtros"
                                       else "No hay promociones disponibles",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                            )
                            Spacer(Modifier.height(16.dp))
                            if (filterState.isActive) {
                                TextButton(onClick = { filterState = ExploreFilterState() }) {
                                    Text("Limpiar filtros", color = KlipprPurple)
                                }
                            } else {
                                TextButton(onClick = viewModel::loadActive) {
                                    Text("Buscar promos", color = KlipprPurple)
                                }
                            }
                        }
                    }
                } else {
                    sections.forEach { category ->
                        val promos = groupedPromos[category] ?: emptyList()
                        item(key = "section_${category.name}") {
                            ExploreCategorySection(
                                category = category,
                                promotions = promos,
                                onPromotionClick = onPromotionClick,
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

            // Overlay: scrim + panel de filtros (US-02)
            AnimatedVisibility(
                visible = showFilterPanel,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                // Scrim transparente para cerrar el panel al tocar fuera
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { showFilterPanel = false },
                )
            }

            AnimatedVisibility(
                visible = showFilterPanel,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 78.dp, end = 16.dp),
            ) {
                FilterPanel(
                    filterState = filterState,
                    onDiscountTypeClick = {
                        filterState = filterState.copy(
                            discountType = when (filterState.discountType) {
                                null -> DiscountType.PERCENTAGE
                                DiscountType.PERCENTAGE -> DiscountType.FIXED_AMOUNT
                                DiscountType.FIXED_AMOUNT -> null
                            },
                        )
                    },
                    onAvailabilityClick = {
                        filterState = filterState.copy(availableOnly = !filterState.availableOnly)
                    },
                    onPopularityClick = {
                        filterState = filterState.copy(sortByPopularity = !filterState.sortByPopularity)
                    },
                    onLocationClick = {
                        filterState = filterState.copy(hasLocationOnly = !filterState.hasLocationOnly)
                    },
                )
            }
        }
    }
}

@Composable
private fun ExploreSearchRow(
    query: String,
    isFilterActive: Boolean,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, KlipprLavender, RoundedCornerShape(12.dp))
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar", color = Color(0xFFAAAAAA), fontSize = 15.sp) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = KlipprPurple,
                ),
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(KlipprPurple, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }

        // Botón filtro: se tiñe de lavanda y muestra badge cuando hay filtros activos.
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.5.dp,
                    if (isFilterActive) KlipprPurple else KlipprLavender,
                    RoundedCornerShape(12.dp),
                )
                .background(if (isFilterActive) KlipprLavender else Color.White)
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Tune,
                contentDescription = "Filtros",
                tint = if (isFilterActive) KlipprPurple else TextSecondary,
                modifier = Modifier.size(22.dp),
            )
            if (isFilterActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(8.dp)
                        .background(KlipprPurple, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun FilterPanel(
    filterState: ExploreFilterState,
    onDiscountTypeClick: () -> Unit,
    onAvailabilityClick: () -> Unit,
    onPopularityClick: () -> Unit,
    onLocationClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(262.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            FilterRow(
                label = "Tipo de descuento",
                isActive = filterState.discountType != null,
                badge = when (filterState.discountType) {
                    DiscountType.PERCENTAGE -> "Porcentaje"
                    DiscountType.FIXED_AMOUNT -> "Monto fijo"
                    null -> null
                },
                onClick = onDiscountTypeClick,
            )
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            FilterRow(
                label = "Disponibilidad",
                isActive = filterState.availableOnly,
                badge = if (filterState.availableOnly) "Con cupo" else null,
                onClick = onAvailabilityClick,
            )
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            FilterRow(
                label = "Popularidad",
                isActive = filterState.sortByPopularity,
                badge = if (filterState.sortByPopularity) "Mayor rating" else null,
                onClick = onPopularityClick,
            )
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            FilterRow(
                label = "Ubicación",
                isActive = filterState.hasLocationOnly,
                badge = if (filterState.hasLocationOnly) "Con dirección" else null,
                onClick = onLocationClick,
            )
        }
    }
}

@Composable
private fun FilterRow(
    label: String,
    isActive: Boolean,
    badge: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = label,
                fontSize = 15.sp,
                color = if (isActive) KlipprPurple else TextPrimary,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            )
            if (badge != null) {
                Text(badge, fontSize = 11.sp, color = KlipprPurple)
            }
        }
        if (isActive) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = KlipprPurple,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun ExploreCategorySection(
    category: PromotionCategory,
    promotions: List<Promotion>,
    onPromotionClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(22.dp)
                        .background(KlipprPurple, RoundedCornerShape(2.dp)),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = category.displayName(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                )
            }
            Text(
                text = "Ver más",
                color = KlipprPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { },
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(promotions, key = { it.id }) { promo ->
                ExplorePromoCard(promotion = promo, onClick = { onPromotionClick(promo.id) })
            }
        }
    }
}

@Composable
private fun ExplorePromoCard(promotion: Promotion, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(160.dp),
    ) {
        Column {
            AsyncImage(
                model = promotion.imageUrl,
                contentDescription = promotion.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFE8E8E8)),
            )
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = promotion.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = promotion.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Más info.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ExploreBottomBar() {
    val inactive = TextSecondary
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Group, contentDescription = "Comunidad") },
            label = { Text("Comunidad", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactive, unselectedTextColor = inactive),
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactive, unselectedTextColor = inactive),
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos") },
            label = { Text("Favoritos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactive, unselectedTextColor = inactive),
        )
        NavigationBarItem(
            selected = true, onClick = {},
            icon = { Icon(Icons.Default.Apps, contentDescription = "Promos") },
            label = { Text("Promos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KlipprPurple, selectedTextColor = KlipprPurple,
                indicatorColor = KlipprLavender,
                unselectedIconColor = inactive, unselectedTextColor = inactive,
            ),
        )
    }
}

// Mapea el enum a nombre visible en español para el encabezado de sección.
private fun PromotionCategory.displayName(): String = when (this) {
    PromotionCategory.FOOD -> "Comida"
    PromotionCategory.BEAUTY -> "Belleza"
    PromotionCategory.HEALTH -> "Salud"
    PromotionCategory.EDUCATION -> "Educación"
    PromotionCategory.ENTERTAINMENT -> "Entretenimiento"
    PromotionCategory.SPORTS -> "Deportes"
    PromotionCategory.SERVICES -> "Servicios"
    PromotionCategory.TECHNOLOGY -> "Tecnología"
    PromotionCategory.OTHER -> "Otros"
}
