package com.example.klippr.favorites.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.ui.theme.KlipprPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoriteViewModel: FavoriteViewModel,
    promotionViewModel: PromotionViewModel,
    userId: String,
    onNavigateComunidad: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePromos: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
){
    val state by favoriteViewModel.state.collectAsStateWithLifecycle()
    val promoState by promotionViewModel.listState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) { favoriteViewModel.loadFavorites(userId) }
    LaunchedEffect(Unit) { promotionViewModel.loadAll() }

    val promoMap = remember(promoState.promotions) {
        promoState.promotions.associateBy { it.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KlipprPurple,
                    titleContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.FAVORITOS,
                onComunidad = onNavigateComunidad,
                onInicio = onNavigateHome,
                onFavoritos = {},
                onPromos = onNavigatePromos,
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = KlipprPurple)
                state.error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("⚠️", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Ocurrió un error",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                    )
                }
                state.isEmpty -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("🔖", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Aún no tienes favoritos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Guarda promos desde la pantalla de Promos",
                        fontSize = 13.sp,
                        color = Color.LightGray,
                    )
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.favorites) { fav ->
                        FavoriteCard(
                            favorite = fav,
                            promotion = promoMap[fav.promotionId],
                            onRemove = { favoriteViewModel.deleteFavorite(fav.favoriteId, userId) },
                            onViewDetail = { onNavigateToDetail(fav.promotionId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    favorite: Favorite,
    promotion: Promotion?,
    onRemove: () -> Unit,
    onViewDetail: () -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = promotion?.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(promotion?.title ?: "Promoción", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(promotion?.businessName ?: "", fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = KlipprPurple)
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Eliminar de favoritos") },
                            onClick = { menuExpanded = false; onRemove() },
                        )
                    }
                }
            }

            promotion?.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(Modifier.padding(12.dp)) {
                Text(
                    promotion?.description ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Text(
                    promotion?.locationName ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onViewDetail,
                        colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text("Ver más detalles", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
