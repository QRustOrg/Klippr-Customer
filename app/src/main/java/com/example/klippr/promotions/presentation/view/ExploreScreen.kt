package com.example.klippr.promotions.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.painterResource
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
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.shared.presentation.component.rememberPromoDrawableId
import com.example.klippr.ui.theme.KlipprTextDark
import com.example.klippr.ui.theme.KlipprTextGray

// @author Samuel Bonifacio

private val KlipprPurple = Color(0xFF887BF3)
private val KlipprLavender = Color(0xFFF0D8FF)
private val TextPrimary = KlipprTextDark
private val TextSecondary = KlipprTextGray

/** Estado local de los filtros activos en la pantalla de exploración (US-02). */
private data class ExploreFilterState(
    val discountType: DiscountType? = null,
    val selectedCategory: PromotionCategory? = null,
    val selectedLocation: String? = null,
    val availableOnly: Boolean = false,
    val sortByPopularity: Boolean = false,
) {
    val isActive: Boolean
        get() = discountType != null ||
            selectedCategory != null ||
            selectedLocation != null ||
            availableOnly ||
            sortByPopularity

    // Número de filtros activos para el badge del botón de filtro.
    val activeCount: Int
        get() = (if (discountType != null) 1 else 0) +
            (if (selectedCategory != null) 1 else 0) +
            (if (selectedLocation != null) 1 else 0) +
            (if (availableOnly) 1 else 0) +
            (if (sortByPopularity) 1 else 0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: PromotionViewModel,
    redemptionViewModel: RedemptionViewModel,
    onBack: () -> Unit,
    onNavigateToQr: (String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToMisPromos: () -> Unit = {},
    onAddFavorite: (promotionId: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
    val businessNames by viewModel.businessNames.collectAsStateWithLifecycle()

    // La exploración consume solo promociones activas (GET /api/promotions/active),
    // las mismas que publica la app Flutter de los negocios.
    LaunchedEffect(Unit) { viewModel.loadActive() }

    var searchQuery by remember { mutableStateOf("") }
    var showFilterPanel by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(ExploreFilterState()) }
    // Id de la promo seleccionada → abre el modal con su detalle. Se guarda el id (no el objeto)
    // para que el favorito/bookmark recomponga en vivo al togglear.
    var selectedPromoId by remember { mutableStateOf<String?>(null) }

    // Al generarse el código de redención, cierra el modal y navega a la pantalla del QR.
    LaunchedEffect(redemptionState.generated) {
        redemptionState.generated?.let { code ->
            selectedPromoId = null
            onNavigateToQr(code.id)
            redemptionViewModel.consumeGenerated()
        }
    }

    // Aplica los filtros activos a la lista de promociones.
    val filteredPromos = remember(state.promotions, filterState) {
        var result = state.promotions
        filterState.discountType?.let { dt -> result = result.filter { it.discountType == dt } }
        filterState.selectedCategory?.let { category -> result = result.filter { it.category == category } }
        filterState.selectedLocation?.let { location ->
            result = result.filter { it.locationName?.trim() == location }
        }
        if (filterState.availableOnly) result = result.filter { it.availableRedemptions > it.currentRedemptions }
        if (filterState.sortByPopularity) result = result.sortedByDescending { it.rating ?: 0.0 }
        result
    }

    val availableCategories = remember(state.promotions) {
        PromotionCategory.entries.filter { category -> state.promotions.any { it.category == category } }
    }
    val availableLocations = remember(state.promotions) {
        state.promotions
            .mapNotNull { it.locationName?.trim()?.takeIf(String::isNotBlank) }
            .distinct()
            .sorted()
    }

    LaunchedEffect(availableLocations, filterState.selectedLocation) {
        val selectedLocation = filterState.selectedLocation
        if (selectedLocation != null && selectedLocation !in availableLocations) {
            filterState = filterState.copy(selectedLocation = null)
        }
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
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.PROMOS,
                onComunidad = onNavigateToCommunity,
                onInicio = onNavigateToHome,
                onFavoritos = onNavigateToMisPromos,
                onPromos = {},
            )
        },
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
                        activeFilterCount = filterState.activeCount,
                        onQueryChange = { searchQuery = it; viewModel.onActiveSearchQueryChange(it) },
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
                                onPromotionClick = { id -> selectedPromoId = id },
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
                    availableCategories = availableCategories,
                    availableLocations = availableLocations,
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
                    onCategoryClick = { category ->
                        filterState = filterState.copy(
                            selectedCategory = category.takeIf { it != filterState.selectedCategory },
                        )
                    },
                    onLocationClick = { location ->
                        filterState = filterState.copy(
                            selectedLocation = location.takeIf { it != filterState.selectedLocation },
                        )
                    },
                )
            }

            // Modal (Dialog centrado) con el detalle de la promo seleccionada.
            val selectedPromo = filteredPromos.find { it.id == selectedPromoId }
            if (selectedPromo != null) {
                LaunchedEffect(selectedPromo.businessId) {
                    viewModel.loadBusinessName(selectedPromo.businessId)
                }
                PromoModalDialog(
                    promotion = selectedPromo,
                    businessName = businessNames[selectedPromo.businessId],
                    isGenerating = redemptionState.isGenerating,
                    errorMessage = redemptionState.error,
                    onDismiss = { selectedPromoId = null; redemptionViewModel.consumeError() },
                    onToggleFavorite = {
                        viewModel.toggleFavorite(selectedPromo.id, !selectedPromo.isFavorite)
                        if (!selectedPromo.isFavorite) onAddFavorite(selectedPromo.id)
                    },
                    onGenerateQr = { redemptionViewModel.generate(selectedPromo) },
                )
            }
        }
    }
}

// Modal (Dialog centrado) 1:1 con el mockup: imagen + X, nombre del negocio + bookmark + share,
// descripción, divisor, Cantidad/Vigencia/Lugar, checkbox de términos que gatea "Generar QR".
@Composable
private fun PromoModalDialog(
    promotion: Promotion,
    businessName: String?,
    isGenerating: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onGenerateQr: () -> Unit,
) {
    val context = LocalContext.current
    var accepted by remember(promotion.id) { mutableStateOf(false) }
    // Encabezado: nombre del negocio resuelto; fallback al nombre embebido o al título.
    val heading = businessName ?: promotion.businessName?.ifBlank { null } ?: promotion.title

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFDF4FC))
                .verticalScroll(rememberScrollState()),
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                val resId = rememberPromoDrawableId(promotion.imageKey)
                if (resId != 0) {
                    Image(
                        painter = painterResource(resId),
                        contentDescription = promotion.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    AsyncImage(
                        model = promotion.imageUrl,
                        contentDescription = promotion.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().background(Color(0xFFE8E8E8)),
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = heading,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (promotion.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (promotion.isFavorite) "Quitar de guardados" else "Guardar",
                            tint = TextPrimary,
                        )
                    }
                    IconButton(onClick = {
                        val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, "${promotion.title}\n${promotion.description}")
                        }
                        context.startActivity(android.content.Intent.createChooser(send, "Compartir"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = TextPrimary)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(promotion.description, fontSize = 16.sp, color = TextPrimary, lineHeight = 22.sp)
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = KlipprPurple.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(Modifier.height(16.dp))
                InfoLine("Cantidad:", "${promotion.availableRedemptions} disponibles")
                Spacer(Modifier.height(10.dp))
                InfoLine("Vigencia:", "Hasta el ${formatVigencia(promotion.endDate)}")
                promotion.termsAndConditions?.takeIf { it.isNotBlank() }?.let { terms ->
                    Spacer(Modifier.height(10.dp))
                    InfoLine("Condiciones:", terms)
                }
                promotion.locationName?.takeIf { it.isNotBlank() }?.let { loc ->
                    Spacer(Modifier.height(10.dp))
                    InfoLine("Lugar:", loc)
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = { accepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = KlipprPurple),
                    )
                    Text("Acepto los ", fontSize = 14.sp, color = TextPrimary)
                    Text("términos y condiciones", fontSize = 14.sp, color = KlipprPurple, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onGenerateQr,
                    enabled = accepted && !isGenerating,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KlipprPurple,
                        disabledContainerColor = Color(0xFFCFC6E8),
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(48.dp),
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Generar QR", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
                if (errorMessage != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(errorMessage, color = Color(0xFFD3503F), fontSize = 13.sp, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
        Spacer(Modifier.width(6.dp))
        Text(value, fontSize = 15.sp, color = TextPrimary)
    }
}

// "Hasta el 20 de diciembre de 2026" a partir de endDate (Instant).
private fun formatVigencia(instant: java.time.Instant): String =
    instant.atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .format(java.time.format.DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", java.util.Locale("es")))

@Composable
private fun ExploreSearchRow(
    query: String,
    activeFilterCount: Int,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
) {
    val isFilterActive = activeFilterCount > 0
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
                // Badge numérico con la cantidad de filtros activos.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(18.dp)
                        .background(Color(0xFFE53935), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = activeFilterCount.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPanel(
    filterState: ExploreFilterState,
    availableCategories: List<PromotionCategory>,
    availableLocations: List<String>,
    onDiscountTypeClick: () -> Unit,
    onAvailabilityClick: () -> Unit,
    onPopularityClick: () -> Unit,
    onCategoryClick: (PromotionCategory) -> Unit,
    onLocationClick: (String) -> Unit,
) {
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var isLocationExpanded by remember { mutableStateOf(false) }

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
                label = "Categoría",
                isActive = filterState.selectedCategory != null,
                badge = filterState.selectedCategory?.displayName(),
                isExpanded = isCategoryExpanded,
                onClick = { isCategoryExpanded = !isCategoryExpanded },
            )
            AnimatedVisibility(visible = isCategoryExpanded) {
                FilterOptionsColumn {
                    if (availableCategories.isEmpty()) {
                        EmptyFilterOption("Sin categorías")
                    } else {
                        availableCategories.forEach { category ->
                            FilterOptionRow(
                                label = category.displayName(),
                                isSelected = filterState.selectedCategory == category,
                                onClick = { onCategoryClick(category) },
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            FilterRow(
                label = "Ubicación",
                isActive = filterState.selectedLocation != null,
                badge = filterState.selectedLocation,
                isExpanded = isLocationExpanded,
                enabled = availableLocations.isNotEmpty(),
                onClick = {
                    if (availableLocations.isNotEmpty()) {
                        isLocationExpanded = !isLocationExpanded
                    }
                },
            )
            AnimatedVisibility(visible = isLocationExpanded) {
                FilterOptionsColumn {
                    if (availableLocations.isEmpty()) {
                        EmptyFilterOption("Sin ubicaciones")
                    } else {
                        availableLocations.forEach { location ->
                            FilterOptionRow(
                                label = location,
                                isSelected = filterState.selectedLocation == location,
                                onClick = { onLocationClick(location) },
                            )
                        }
                    }
                }
            }
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
        }
    }
}

@Composable
private fun FilterRow(
    label: String,
    isActive: Boolean,
    badge: String?,
    isExpanded: Boolean? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val rowColor = when {
            !enabled -> TextSecondary.copy(alpha = 0.55f)
            isActive -> KlipprPurple
            else -> TextPrimary
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                color = rowColor,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            )
            val helperText = badge ?: if (!enabled) "No disponible" else null
            if (helperText != null) {
                Text(
                    text = helperText,
                    fontSize = 11.sp,
                    color = if (enabled) KlipprPurple else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        when {
            isExpanded != null -> {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (!enabled) TextSecondary.copy(alpha = 0.55f) else if (isActive) KlipprPurple else TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            isActive -> {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = KlipprPurple,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun FilterOptionsColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFBF8FF))
            .padding(vertical = 4.dp),
        content = content,
    )
}

@Composable
private fun FilterOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 32.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isSelected) KlipprPurple else TextPrimary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = KlipprPurple,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun EmptyFilterOption(label: String) {
    Text(
        text = label,
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier.padding(start = 32.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
    )
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
            val imageModifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color(0xFFE8E8E8))
            val resId = rememberPromoDrawableId(promotion.imageKey)
            if (resId != 0) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = promotion.title,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            } else {
                // Sin drawable local para este imageKey: cae a la imagen remota (o placeholder gris).
                AsyncImage(
                    model = promotion.imageUrl,
                    contentDescription = promotion.title,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }
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
