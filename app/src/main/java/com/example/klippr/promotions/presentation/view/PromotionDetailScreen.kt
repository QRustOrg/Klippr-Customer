package com.example.klippr.promotions.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel

// @author Samuel Bonifacio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDetailScreen(
    promotionId: String,
    viewModel: PromotionViewModel,
    onBack: () -> Unit,
    onApplyDiscount: (Promotion) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(promotionId) { viewModel.loadDetail(promotionId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    state.promotion?.let { promo ->
                        IconButton(onClick = { viewModel.toggleFavorite(promo.id, !promo.isFavorite) }) {
                            Icon(
                                imageVector = if (promo.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = Color.White,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

            state.error != null -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { Text(state.error!!, color = Color.Gray) }

            state.promotion != null -> PromotionDetailContent(
                promotion = state.promotion!!,
                onApplyDiscount = onApplyDiscount,
                topPadding = innerPadding.calculateTopPadding(),
            )
        }
    }
}


@Composable
private fun PromotionDetailContent(
    promotion: Promotion,
    onApplyDiscount: (Promotion) -> Unit,
    topPadding: androidx.compose.ui.unit.Dp,
) {
    var termsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Hero image (extends behind top bar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
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
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // Info card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            // Business name + rating
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = promotion.businessName ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                )
                promotion.rating?.let { r ->
                    Text("⭐ ${"%.1f".format(r)}", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(6.dp))

            // Title
            Text(
                text = promotion.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(10.dp))

            // Discount badge
            DiscountBadge(value = promotion.discountValue, type = promotion.discountType)

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                text = promotion.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 22.sp,
            )

            // Terms and conditions (expandable)
            promotion.termsAndConditions?.let { terms ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (termsExpanded) "▲ Ocultar términos y condiciones"
                           else "▼ Ver términos y condiciones",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { termsExpanded = !termsExpanded },
                )
                if (termsExpanded) {
                    Spacer(Modifier.height(8.dp))
                    Text(terms, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            // Redemptions counter
            RedemptionsInfo(
                available = promotion.availableRedemptions,
                current = promotion.currentRedemptions,
            )

            // Location
            promotion.locationName?.let { location ->
                Spacer(Modifier.height(8.dp))
                Text("📍 $location", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }

            Spacer(Modifier.height(28.dp))

            // CTA button
            Button(
                onClick = { onApplyDiscount(promotion) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Aplicar Descuento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
private fun RedemptionsInfo(available: Int, current: Int) {
    val remaining = (available - current).coerceAtLeast(0)
    val isUnlimited = available == Int.MAX_VALUE
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = if (isUnlimited) "  Canjes ilimitados disponibles"
                   else "  $remaining canjes disponibles",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
        )
    }
}

