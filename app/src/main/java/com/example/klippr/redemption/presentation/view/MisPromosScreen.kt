package com.example.klippr.redemption.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.community.presentation.view.ReviewBottomSheet
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.shared.presentation.component.rememberPromoDrawableId
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.shared.presentation.component.RemoteFavoriteHeartButton
import com.example.klippr.shared.presentation.component.discountLabel
import com.example.klippr.ui.theme.KlipprTextDark
import com.example.klippr.ui.theme.KlipprTextGray
import com.example.klippr.redemption.util.dayBucket
import com.example.klippr.redemption.util.formatHora
import com.example.klippr.redemption.util.formatVence
import com.example.klippr.redemption.util.generateQrBitmap
import com.example.klippr.ui.theme.KlipprLavender
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio

private val TextSecondary = KlipprTextGray
private val TextPrimary = KlipprTextDark
private val ActiveGreenBg = Color(0xFFB9F6CA)
private val ActiveGreenFg = Color(0xFF1B7A3D)
private val SavingsGreen = Color(0xFF1E9E54)
private val CanjeadoBlueBg = Color(0xFFCFE6FF)
private val CanjeadoBlueFg = Color(0xFF1565C0)

private enum class Periodo(val label: String) { TODOS("Todos"), HOY("Hoy"), SEMANA("Esta semana"), MES("Este mes") }
private enum class OrdenMonto(val label: String) { NINGUNO("Sin orden"), MAYOR("Mayor a menor"), MENOR("Menor a mayor") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPromosScreen(
    viewModel: RedemptionViewModel,
    communityViewModel: CommunityViewModel,
    favoriteViewModel: FavoriteViewModel,
    promotionViewModel: PromotionViewModel,
    currentUserId: String,
    initialOuterTab: Int = 0,
    onCodeClick: (String) -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateCommunity: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePromos: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val communityUiState by communityViewModel.uiState.collectAsState()
    val favoriteState by favoriteViewModel.state.collectAsStateWithLifecycle()
    val promotionListState by promotionViewModel.listState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadHistory() }
    LaunchedEffect(currentUserId) { favoriteViewModel.loadFavorites(currentUserId) }

    // 0 = Favoritos, 1 = Archivados, 2 = Mis Promos (activos/canjeados/expirados)
    var outerTab by remember(initialOuterTab) { mutableIntStateOf(initialOuterTab.coerceIn(0, 2)) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var clearTarget by remember { mutableStateOf<List<RedemptionCode>?>(null) }

    // US-13: ReviewBottomSheet como modal sobre MisPromos
    if (communityUiState.isReviewSheetOpen) {
        ReviewBottomSheet(
            uiState = communityUiState,
            onDismiss = { communityViewModel.closeReviewSheet() },
            onRatingChanged = communityViewModel::onRatingChanged,
            onCommentChanged = communityViewModel::onCommentChanged,
            onSubmit = communityViewModel::submitReview,
        )
    }

    clearTarget?.let { codes ->
        AlertDialog(
            onDismissRequest = { clearTarget = null },
            title = { Text("Limpiar promos") },
            text = { Text("Se eliminaran ${codes.size} promos de esta lista.") },
            confirmButton = {
                TextButton(onClick = { viewModel.clear(codes); clearTarget = null }) {
                    Text("Limpiar", color = KlipprPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { clearTarget = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (outerTab) {
                            1 -> "Archivados"
                            2 -> "Mis Promos"
                            else -> "Favoritos"
                        },
                        fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KlipprPurple),
            )
        },
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.FAVORITOS,
                onComunidad = onNavigateCommunity,
                onInicio = onNavigateHome,
                onFavoritos = {},
                onPromos = onNavigatePromos,
            )
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(selectedTabIndex = outerTab, containerColor = Color.White, contentColor = KlipprPurple) {
                Tab(selected = outerTab == 0, onClick = { outerTab = 0 }, text = { Text("Favoritos") })
                Tab(selected = outerTab == 1, onClick = { outerTab = 1 }, text = { Text("Archivados") })
                Tab(selected = outerTab == 2, onClick = { outerTab = 2 }, text = { Text("Mis Promos") })
            }

            when (outerTab) {
                0 -> FavoritesTabContent(
                    favorites = favoriteState.visibleFavorites,
                    isLoading = favoriteState.isLoading,
                    promotions = promotionListState.promotions,
                    emptyText = "Aun no tienes favoritos. Marca una promo con el corazon para verla aqui.",
                    secondaryActionLabel = "Archivar",
                    favoriteViewModel = favoriteViewModel,
                    currentUserId = currentUserId,
                    onVerDetalles = { fav ->
                        favoriteViewModel.openFavoriteDetails(fav.favoriteId, onNavigateToDetail)
                    },
                    onFavoriteSaved = { fav ->
                        promotionViewModel.toggleFavorite(fav.promotionId, true)
                    },
                    onEliminar = { fav ->
                        favoriteViewModel.deleteFavorite(fav.favoriteId, currentUserId) {
                            promotionViewModel.toggleFavorite(fav.promotionId, false)
                        }
                    },
                    onSecondaryAction = { fav ->
                        favoriteViewModel.archiveFavorite(fav.favoriteId, currentUserId) {
                            promotionViewModel.toggleFavorite(fav.promotionId, false)
                        }
                    },
                )
                1 -> FavoritesTabContent(
                    favorites = favoriteState.archivedFavorites,
                    isLoading = favoriteState.isLoading,
                    promotions = promotionListState.promotions,
                    emptyText = "No tienes favoritos archivados.",
                    secondaryActionLabel = "Restaurar",
                    favoriteViewModel = favoriteViewModel,
                    currentUserId = currentUserId,
                    onVerDetalles = { fav ->
                        favoriteViewModel.openFavoriteDetails(fav.favoriteId, onNavigateToDetail)
                    },
                    onFavoriteSaved = { fav ->
                        promotionViewModel.toggleFavorite(fav.promotionId, true)
                    },
                    onEliminar = { fav ->
                        favoriteViewModel.deleteFavorite(fav.favoriteId, currentUserId) {
                            promotionViewModel.toggleFavorite(fav.promotionId, false)
                        }
                    },
                    onSecondaryAction = { fav ->
                        favoriteViewModel.restoreFavorite(fav.favoriteId, currentUserId) {
                            promotionViewModel.toggleFavorite(fav.promotionId, true)
                        }
                    },
                )
                else -> {
                PromosTabRow(
                    selected = selectedTab,
                    counts = intArrayOf(state.active.size, state.redeemed.size, state.expired.size),
                    onSelect = { selectedTab = it },
                )

                when {
                    state.isLoading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = KlipprPurple) }

                    state.error != null -> Box(
                        Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(state.error!!, color = TextSecondary) }

                    else -> when (selectedTab) {
                        0 -> QrCardsList(
                            codes = state.active,
                            emptyText = "No tienes códigos activos",
                            onCodeClick = onCodeClick,
                            onMarkRedeemed = { code -> viewModel.markRedeemed(code) },
                            isBusy = state.isGenerating,
                        )
                        1 -> HistorialList(
                            codes = state.redeemed,
                            onCodeClick = onCodeClick,
                            onClear = { code -> clearTarget = listOf(code) },
                            onLeaveReview = { code ->
                                communityViewModel.openReviewSheetForRedeemed(
                                    code.promotionId,
                                    code.promotionTitle ?: "Promoción",
                                )
                            },
                        )
                        else -> QrCardsList(
                            codes = state.expired,
                            emptyText = "No tienes códigos expirados",
                            onCodeClick = onCodeClick,
                            onMarkRedeemed = null,
                            onClear = { code -> clearTarget = listOf(code) },
                            isBusy = false,
                        )
                    }
                }
            }
            }
        }
    }
}

// ---------- Favoritos: carta con bookmark + menú 3 puntos (Eliminar/Archivar) ----------

@Composable
private fun FavoritesTabContent(
    favorites: List<Favorite>,
    isLoading: Boolean,
    promotions: List<Promotion>,
    emptyText: String,
    secondaryActionLabel: String,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onVerDetalles: (Favorite) -> Unit,
    onFavoriteSaved: (Favorite) -> Unit,
    onEliminar: (Favorite) -> Unit,
    onSecondaryAction: (Favorite) -> Unit,
) {
    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = KlipprPurple)
        }
        favorites.isEmpty() -> EmptyMessage(emptyText)
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(favorites, key = { it.favoriteId }) { fav ->
                val promo = promotions.find { it.id == fav.promotionId }
                FavoriteCard(
                    favorite = fav,
                    promotion = promo,
                    secondaryActionLabel = secondaryActionLabel,
                    favoriteViewModel = favoriteViewModel,
                    currentUserId = currentUserId,
                    onVerDetalles = { onVerDetalles(fav) },
                    onFavoriteSaved = { onFavoriteSaved(fav) },
                    onEliminar = { onEliminar(fav) },
                    onSecondaryAction = { onSecondaryAction(fav) },
                )
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    favorite: Favorite,
    promotion: Promotion?,
    secondaryActionLabel: String,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onVerDetalles: () -> Unit,
    onFavoriteSaved: () -> Unit,
    onEliminar: () -> Unit,
    onSecondaryAction: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFE4DCFB)),
                contentAlignment = Alignment.Center,
            ) {
                val resId = rememberPromoDrawableId(promotion?.imageKey)
                if (resId != 0) {
                    Image(
                        painter = painterResource(resId),
                        contentDescription = promotion?.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = promotion?.businessName ?: "Promoción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = promotion?.title ?: "",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            RemoteFavoriteHeartButton(
                userId = currentUserId,
                promotionId = favorite.promotionId,
                isFavorite = true,
                favoriteViewModel = favoriteViewModel,
                selectedTint = KlipprPurple,
                unselectedTint = TextSecondary,
                backgroundColor = Color.Transparent,
                modifier = Modifier.size(32.dp),
                onSaved = onFavoriteSaved,
            )
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = TextSecondary)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text("Eliminar") }, onClick = { menuOpen = false; onEliminar() })
                    DropdownMenuItem(text = { Text(secondaryActionLabel) }, onClick = { menuOpen = false; onSecondaryAction() })
                }
            }
        }
        TextButton(onClick = onVerDetalles, modifier = Modifier.align(Alignment.End)) {
            Text("Ver más detalles", color = KlipprPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

// Fila de tabs estilo mockup: el seleccionado muestra su conteo y subrayado morado.
@Composable
private fun PromosTabRow(selected: Int, counts: IntArray, onSelect: (Int) -> Unit) {
    val labels = listOf("Activos", "Canjeados", "Expirados")
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { i, label ->
            val isSel = i == selected
            val text = if (isSel) "$label (${counts[i]})" else label
            Column(
                modifier = Modifier.weight(1f).clickable { onSelect(i) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    color = if (isSel) KlipprPurple else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                )
                Spacer(Modifier.height(6.dp))
                if (isSel) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(KlipprPurple),
                    )
                }
            }
        }
    }
}

// ---------- Activos / Expirados: tarjetas con QR (mockup 2) ----------

@Composable
private fun QrCardsList(
    codes: List<RedemptionCode>,
    emptyText: String,
    onCodeClick: (String) -> Unit,
    onMarkRedeemed: ((RedemptionCode) -> Unit)?,
    onClear: ((RedemptionCode) -> Unit)? = null,
    isBusy: Boolean,
) {
    if (codes.isEmpty()) {
        EmptyMessage(emptyText)
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(codes, key = { it.id }) { code ->
            QrCard(
                code = code,
                onClick = { onCodeClick(code.id) },
                onMarkRedeemed = onMarkRedeemed?.let { cb -> { cb(code) } },
                onClear = onClear?.let { cb -> { cb(code) } },
                isBusy = isBusy,
            )
        }
    }
}

@Composable
private fun QrCard(
    code: RedemptionCode,
    onClick: () -> Unit,
    onMarkRedeemed: (() -> Unit)?,
    onClear: (() -> Unit)?,
    isBusy: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(140.dp).clip(RoundedCornerShape(18.dp)).background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                val qr = remember(code.qrContent) { generateQrBitmap(code.qrContent, 220) }
                if (qr != null) {
                    Image(bitmap = qr, contentDescription = "Código QR", modifier = Modifier.size(104.dp))
                } else {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(96.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = code.businessName ?: code.promotionTitle ?: "Promoción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusPill(code.status)
                }
                if (onClear != null) {
                    IconButton(
                        onClick = onClear,
                        enabled = !isBusy,
                        modifier = Modifier.align(Alignment.End).size(32.dp),
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpiar promo", tint = TextSecondary)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(text = code.promotionTitle ?: code.discountLabel(), fontSize = 16.sp, color = TextPrimary)
                Spacer(Modifier.height(10.dp))
                Text(text = code.dateLabel(), fontSize = 13.sp, color = TextSecondary)
            }
        }
        // US-06: marcar como canjeado (llama al endpoint /confirm)
        if (onMarkRedeemed != null) {
            TextButton(
                onClick = onMarkRedeemed,
                enabled = !isBusy,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Marcar como canjeado", color = KlipprPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

// ---------- Canjeados: layout Historial agrupado por día (mockup 3) ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistorialList(
    codes: List<RedemptionCode>,
    onCodeClick: (String) -> Unit,
    onClear: (RedemptionCode) -> Unit,
    onLeaveReview: (RedemptionCode) -> Unit,
) {
    var filterOpen by remember { mutableStateOf(false) }
    var negocio by remember { mutableStateOf<String?>(null) }
    var periodo by remember { mutableStateOf(Periodo.TODOS) }
    var orden by remember { mutableStateOf(OrdenMonto.NINGUNO) }

    val negocios = remember(codes) {
        codes.mapNotNull { it.businessName ?: it.promotionTitle }.distinct()
    }
    val filtered = remember(codes, negocio, periodo, orden) {
        applyFilters(codes, negocio, periodo, orden)
    }

    if (filterOpen) {
        ModalBottomSheet(
            onDismissRequest = { filterOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
        ) {
            FilterSheet(
                negocios = negocios,
                negocioSel = negocio, onNegocio = { negocio = it },
                periodoSel = periodo, onPeriodo = { periodo = it },
                ordenSel = orden, onOrden = { orden = it },
                onLimpiar = { negocio = null; periodo = Periodo.TODOS; orden = OrdenMonto.NINGUNO },
            )
        }
    }

    if (codes.isEmpty()) {
        EmptyMessage("Aún no tienes canjes. Marca un código activo como canjeado.")
        return
    }

    // Agrupa preservando el orden ya filtrado/ordenado.
    val groups = filtered.groupBy { dayBucket(it.redeemedAt) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "filtrar_bar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { filterOpen = true }) {
                    Text("Filtrar", color = KlipprPurple, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
        if (filtered.isEmpty()) {
            item(key = "no_match") { EmptyMessage("Ningún canje coincide con el filtro") }
        }
        groups.forEach { (bucket, items) ->
            item(key = "h_$bucket") {
                Text(
                    text = bucket,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                )
            }
            items(items, key = { it.id }) { code ->
                HistorialCard(
                    code = code,
                    onClick = { onCodeClick(code.id) },
                    onClear = { onClear(code) },
                    onLeaveReview = { onLeaveReview(code) },
                )
            }
        }
    }
}

private fun applyFilters(
    codes: List<RedemptionCode>,
    negocio: String?,
    periodo: Periodo,
    orden: OrdenMonto,
): List<RedemptionCode> {
    val now = System.currentTimeMillis()
    val cutoff = when (periodo) {
        Periodo.TODOS -> null
        Periodo.HOY -> now - 86_400_000L
        Periodo.SEMANA -> now - 7 * 86_400_000L
        Periodo.MES -> now - 30 * 86_400_000L
    }
    var out = codes
    if (negocio != null) {
        out = out.filter { (it.businessName ?: it.promotionTitle) == negocio }
    }
    if (cutoff != null) {
        out = out.filter { it.redeemedAt != null && it.redeemedAt > cutoff }
    }
    out = when (orden) {
        OrdenMonto.NINGUNO -> out.sortedByDescending { it.redeemedAt ?: Long.MIN_VALUE }
        OrdenMonto.MAYOR -> out.sortedByDescending { it.discountAppliedAmount }
        OrdenMonto.MENOR -> out.sortedBy { it.discountAppliedAmount }
    }
    return out
}

@Composable
private fun HistorialCard(
    code: RedemptionCode,
    onClick: () -> Unit,
    onClear: () -> Unit,
    onLeaveReview: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Imagen de la promo (placeholder si no hay drawable)
            Box(
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFE4DCFB)),
                contentAlignment = Alignment.Center,
            ) {
                val resId = rememberPromoDrawableId(code.imageKey)
                if (resId != 0) {
                    Image(
                        painter = painterResource(resId),
                        contentDescription = code.promotionTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = code.businessName ?: code.promotionTitle ?: "Promoción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onClear, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpiar promo", tint = TextSecondary)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${code.discountLabel()} - ${formatHora(code.redeemedAt)}",
                    fontSize = 15.sp,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "-S/%.2f".format(code.discountAppliedAmount),
                        color = SavingsGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    CanjeadoPill()
                }
            }
        }
        // US-13: dejar reseña de una promo usada
        TextButton(onClick = onLeaveReview, modifier = Modifier.align(Alignment.End)) {
            Text("Dejar reseña", color = KlipprPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(
    negocios: List<String>,
    negocioSel: String?, onNegocio: (String?) -> Unit,
    periodoSel: Periodo, onPeriodo: (Periodo) -> Unit,
    ordenSel: OrdenMonto, onOrden: (OrdenMonto) -> Unit,
    onLimpiar: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Filtrar", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
            TextButton(onClick = onLimpiar) { Text("Limpiar", color = KlipprPurple, fontWeight = FontWeight.SemiBold) }
        }

        Spacer(Modifier.height(8.dp))
        FilterGroupLabel("Negocio")
        ChipFlow {
            ChoiceChip(label = "Todos", selected = negocioSel == null) { onNegocio(null) }
            negocios.forEach { n ->
                ChoiceChip(label = n, selected = negocioSel == n) { onNegocio(n) }
            }
        }

        Spacer(Modifier.height(12.dp))
        FilterGroupLabel("Periodo")
        ChipFlow {
            Periodo.entries.forEach { p ->
                ChoiceChip(label = p.label, selected = periodoSel == p) { onPeriodo(p) }
            }
        }

        Spacer(Modifier.height(12.dp))
        FilterGroupLabel("Ordenar por ahorro")
        ChipFlow {
            OrdenMonto.entries.forEach { o ->
                ChoiceChip(label = o.label, selected = ordenSel == o) { onOrden(o) }
            }
        }
    }
}

@Composable
private fun FilterGroupLabel(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
}

// Flujo simple de chips en filas (wrap automático).
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) { content() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = KlipprLavender,
            selectedLabelColor = KlipprPurple,
        ),
    )
}

// ---------- compartidos ----------

@Composable
private fun EmptyMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF7F7F7))
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            Text(text, color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun StatusPill(status: RedemptionStatus) {
    val (text, bg, fg) = when (status) {
        RedemptionStatus.ACTIVE -> Triple("Activo", ActiveGreenBg, ActiveGreenFg)
        RedemptionStatus.REDEEMED -> Triple("Canjeado", CanjeadoBlueBg, CanjeadoBlueFg)
        RedemptionStatus.EXPIRED -> Triple("Expirado", Color(0xFFEEEEEE), TextSecondary)
    }
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 16.dp, vertical = 7.dp),
    ) {
        Text(text, color = fg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun CanjeadoPill() {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(CanjeadoBlueBg).padding(horizontal = 16.dp, vertical = 7.dp),
    ) {
        Text("Canjeado", color = CanjeadoBlueFg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

private fun RedemptionCode.discountLabel(): String =
    discountLabel(discountType, discountValue ?: discountAppliedAmount)

private fun RedemptionCode.dateLabel(): String = when (status) {
    RedemptionStatus.REDEEMED -> "Usado: ${formatVence(redeemedAt)}"
    RedemptionStatus.EXPIRED -> "Venció: ${formatVence(expiresAt)}"
    RedemptionStatus.ACTIVE -> "Vence: ${formatVence(expiresAt)}"
}
