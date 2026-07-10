package com.example.klippr.promotions.presentation.views

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.shared.presentation.components.DiscountBadge
import com.example.klippr.shared.presentation.components.RemoteFavoriteHeartButton

// @author Samuel Bonifacio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionListScreen(
    viewModel: PromotionViewModel,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onPromotionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    val favoriteState by favoriteViewModel.state.collectAsStateWithLifecycle()
    val favoriteByPromotionId = favoriteState.visibleFavorites.associateBy { it.promotionId }

    LaunchedEffect(currentUserId) {
        favoriteViewModel.loadFavorites(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Buscar promociones...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )

            // Category filter chips
            CategoryFilterRow(
                selectedCategory = state.selectedCategory,
                onCategorySelected = viewModel::onCategorySelected,
            )

            Spacer(Modifier.height(8.dp))

            // Content area
            when {
                state.isLoading -> LoadingContent()
                state.error != null -> ErrorContent(
                    message = state.error!!,
                    onRetry = viewModel::loadAll,
                )
                state.isEmpty -> EmptyPromotionsContent(onRetry = viewModel::loadAll)
                else -> PromotionLazyList(
                    promotions = state.displayed,
                    favoriteByPromotionId = favoriteByPromotionId,
                    favoriteViewModel = favoriteViewModel,
                    currentUserId = currentUserId,
                    onItemClick = onPromotionClick,
                    onFavoriteSaved = { id -> viewModel.toggleFavorite(id, true) },
                )
            }
        }
    }
}


@Composable
private fun CategoryFilterRow(
    selectedCategory: PromotionCategory?,
    onCategorySelected: (PromotionCategory?) -> Unit,
) {
    val categories = listOf(
        null to "Todos",
        PromotionCategory.FOOD to "🍔 Comida",
        PromotionCategory.BEAUTY to "💄 Belleza",
        PromotionCategory.HEALTH to "💊 Salud",
        PromotionCategory.EDUCATION to "📚 Educación",
        PromotionCategory.ENTERTAINMENT to "🎬 Ocio",
        PromotionCategory.SPORTS to "⚽ Deportes",
        PromotionCategory.SERVICES to "🛠 Servicios",
        PromotionCategory.TECHNOLOGY to "💻 Tecnología",
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories) { (category, label) ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(label, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                ),
            )
        }
    }
}


@Composable
private fun PromotionLazyList(
    promotions: List<Promotion>,
    favoriteByPromotionId: Map<String, com.example.klippr.favorites.domain.model.Favorite>,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onItemClick: (String) -> Unit,
    onFavoriteSaved: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(promotions, key = { it.id }) { promotion ->
            PromotionCard(
                promotion = promotion,
                isFavorite = favoriteByPromotionId.containsKey(promotion.id),
                favoriteViewModel = favoriteViewModel,
                currentUserId = currentUserId,
                onClick = { onItemClick(promotion.id) },
                onFavoriteSaved = { onFavoriteSaved(promotion.id) },
            )
        }
    }
}


@Composable
fun PromotionCard(
    promotion: Promotion,
    isFavorite: Boolean,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onClick: () -> Unit,
    onFavoriteSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp),
        ) {
            // Thumbnail
            AsyncImage(
                model = promotion.imageUrl,
                contentDescription = promotion.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Negocio: ${promotion.businessName?.takeIf { it.isNotBlank() } ?: "No disponible"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = promotion.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                DiscountBadge(
                    type = promotion.discountType,
                    value = promotion.discountValue,
                )
                promotion.rating?.let { r ->
                    Spacer(Modifier.height(4.dp))
                    Text("⭐ ${"%.1f".format(r)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            RemoteFavoriteHeartButton(
                userId = currentUserId,
                promotionId = promotion.id,
                isFavorite = isFavorite,
                favoriteViewModel = favoriteViewModel,
                modifier = Modifier.size(44.dp),
                backgroundColor = Color.Transparent,
                selectedTint = MaterialTheme.colorScheme.primary,
                unselectedTint = Color.LightGray,
                onSaved = onFavoriteSaved,
            )
        }
    }
}


@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Ocurrió un error", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun EmptyPromotionsContent(onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("😕", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text("No hay promociones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Intenta de nuevo más tarde", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onRetry) { Text("Buscar promos") }
    }
}
