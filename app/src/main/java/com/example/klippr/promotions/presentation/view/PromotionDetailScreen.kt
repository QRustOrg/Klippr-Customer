package com.example.klippr.promotions.presentation.view

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.domain.model.redemptionBlockedMessage
import com.example.klippr.redemption.util.formatVence
import com.example.klippr.shared.presentation.component.discountLabel
import com.example.klippr.shared.presentation.component.RemoteFavoriteHeartButton
import com.example.klippr.shared.presentation.component.rememberPromoDrawableId
import com.example.klippr.ui.theme.KlipprCardPink
import com.example.klippr.ui.theme.KlipprPurple
import com.example.klippr.ui.theme.KlipprTextDark
import com.example.klippr.ui.theme.KlipprTextGray

// @author Samuel Bonifacio

@Composable
fun PromotionDetailScreen(
    promotionId: String,
    viewModel: PromotionViewModel,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onBack: () -> Unit,
    onApplyDiscount: (Promotion) -> Unit,
    onNavigateToReviews: (promotionId: String) -> Unit = { _ -> },
    isGenerating: Boolean = false,
    errorMessage: String? = null,
    isFavoriteOverride: Boolean? = null,
    onFavoriteSaved: (promotionId: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()
    val businessNames by viewModel.businessNames.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(promotionId) { viewModel.loadDetail(promotionId) }
    LaunchedEffect(state.promotion?.businessId) {
        state.promotion?.businessId?.let(viewModel::loadBusinessName)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KlipprPurple)
            }

            state.error != null -> ErrorState(message = state.error.orEmpty(), onBack = onBack)

            state.promotion != null -> {
                val promotion = state.promotion!!
                val businessDisplayName = promotion.businessName?.takeIf { it.isNotBlank() }
                    ?: businessNames[promotion.businessId]?.takeIf { it.isNotBlank() }
                    ?: "Negocio no disponible"

                PromotionDetailContent(
                    promotion = promotion,
                    businessDisplayName = businessDisplayName,
                    isGenerating = isGenerating,
                    errorMessage = errorMessage,
                    onApplyDiscount = onApplyDiscount,
                    onBack = onBack,
                    isFavorite = isFavoriteOverride ?: promotion.isFavorite,
                    favoriteViewModel = favoriteViewModel,
                    currentUserId = currentUserId,
                    onShare = { sharePromotion(context, promotion, businessDisplayName) },
                    onFavoriteSaved = {
                        viewModel.toggleFavorite(promotion.id, true)
                        onFavoriteSaved(promotion.id)
                    },
                    onNavigateToReviews = { onNavigateToReviews(promotion.id) },
                )
            }
        }
    }
}

@Composable
private fun PromotionDetailContent(
    promotion: Promotion,
    businessDisplayName: String,
    isGenerating: Boolean,
    errorMessage: String?,
    onApplyDiscount: (Promotion) -> Unit,
    onBack: () -> Unit,
    isFavorite: Boolean,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onShare: () -> Unit,
    onFavoriteSaved: () -> Unit,
    onNavigateToReviews: () -> Unit,
) {
    var accepted by remember(promotion.id) { mutableStateOf(false) }
    val blockedMessage = promotion.redemptionBlockedMessage()
    val visibleError = errorMessage ?: blockedMessage

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            ) {
                PromotionHeroImage(promotion)
                TopActions(
                    promotionId = promotion.id,
                    isFavorite = isFavorite,
                    favoriteViewModel = favoriteViewModel,
                    currentUserId = currentUserId,
                    onBack = onBack,
                    onShare = onShare,
                    onFavoriteSaved = onFavoriteSaved,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .padding(start = 22.dp, top = 24.dp, end = 22.dp, bottom = 24.dp),
            ) {
                Text(
                    text = businessDisplayName,
                    color = KlipprPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = promotion.title,
                    color = KlipprTextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = buildSubtitle(promotion.category, promotion.locationName, promotion.description),
                    color = KlipprTextGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                promotion.rating?.let { rating ->
                    Spacer(Modifier.height(16.dp))
                    RatingAndReviews(
                        rating = rating,
                        onNavigateToReviews = onNavigateToReviews,
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = KlipprPurple.copy(alpha = 0.35f), thickness = 1.dp)
                Spacer(Modifier.height(16.dp))

                BusinessSection(
                    businessDisplayName = businessDisplayName,
                    category = promotion.category,
                    location = promotion.locationName,
                )

                val remaining = (promotion.availableRedemptions - promotion.currentRedemptions).coerceAtLeast(0)
                if (remaining in 1..9) {
                    Spacer(Modifier.height(16.dp))
                    UrgencyBanner(remaining = remaining)
                }

                Spacer(Modifier.height(18.dp))
                DetailInfoLine("Negocio:", businessDisplayName)
                Spacer(Modifier.height(10.dp))
                DetailInfoLine("Cantidad:", redemptionsLabel(promotion))
                Spacer(Modifier.height(10.dp))
                DetailInfoLine("Vigencia:", "Hasta el ${formatVence(promotion.endDate.toEpochMilli())}")

                promotion.termsAndConditions?.takeIf { it.isNotBlank() }?.let { terms ->
                    Spacer(Modifier.height(10.dp))
                    DetailInfoLine("Condiciones:", terms)
                }

                promotion.locationName?.takeIf { it.isNotBlank() }?.let { location ->
                    Spacer(Modifier.height(10.dp))
                    DetailInfoLine("Lugar:", location)
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { accepted = !accepted },
                ) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = { accepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = KlipprPurple),
                    )
                    Text("Acepto los ", fontSize = 14.sp, color = KlipprTextDark)
                    Text(
                        text = "terminos y condiciones",
                        fontSize = 14.sp,
                        color = KlipprPurple,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                if (visibleError != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = visibleError,
                        color = Color(0xFFD3503F),
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        BottomActionBar(
            discountLabel = discountLabel(promotion.discountType, promotion.discountValue),
            enabled = accepted && !isGenerating && blockedMessage == null,
            isGenerating = isGenerating,
            onApplyDiscount = { onApplyDiscount(promotion) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun PromotionHeroImage(promotion: Promotion) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFFE8E8E8)),
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
}

@Composable
private fun TopActions(
    promotionId: String,
    isFavorite: Boolean,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavoriteSaved: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingIconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = KlipprTextDark,
            )
        }
        Row {
            FloatingIconButton(onClick = onShare) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Compartir",
                    tint = KlipprTextDark,
                )
            }
            Spacer(Modifier.width(8.dp))
            RemoteFavoriteHeartButton(
                userId = currentUserId,
                promotionId = promotionId,
                isFavorite = isFavorite,
                favoriteViewModel = favoriteViewModel,
                selectedTint = KlipprPurple,
                unselectedTint = KlipprTextDark,
                backgroundColor = Color.White.copy(alpha = 0.92f),
                onSaved = onFavoriteSaved,
            )
        }
    }
}

@Composable
private fun FloatingIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f)),
        content = content,
    )
}

@Composable
private fun BusinessSection(
    businessDisplayName: String,
    category: PromotionCategory,
    location: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BusinessAvatar(name = businessDisplayName)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Promoción de $businessDisplayName",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = KlipprTextDark,
            )
            Text(
                text = listOfNotNull(
                    categoryLabel(category),
                    location?.takeIf { it.isNotBlank() },
                ).joinToString(" · "),
                fontSize = 14.sp,
                color = KlipprTextGray,
            )
        }
    }
}

@Composable
private fun BusinessAvatar(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(KlipprPurple),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RatingAndReviews(
    rating: Double,
    onNavigateToReviews: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = String.format("%.1f", rating),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = KlipprTextDark,
        )
        Spacer(Modifier.width(6.dp))
        StarRatingDisplay(rating = rating.toInt())
        Spacer(Modifier.width(12.dp))
        TextButton(onClick = onNavigateToReviews) {
            Text(
                text = "Ver reseñas",
                color = KlipprPurple,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun StarRatingDisplay(rating: Int) {
    Row {
        (1..5).forEach { i ->
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun UrgencyBanner(remaining: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KlipprCardPink)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = KlipprPurple,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (remaining == 1) "¡Última unidad! Solo queda 1 canje" else "¡Últimas unidades! Solo quedan $remaining canjes",
            color = KlipprTextDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun BottomActionBar(
    discountLabel: String,
    enabled: Boolean,
    isGenerating: Boolean,
    onApplyDiscount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = discountLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = KlipprTextDark,
                )
                Text(
                    text = "Descuento",
                    fontSize = 12.sp,
                    color = KlipprTextGray,
                )
            }
            Button(
                onClick = onApplyDiscount,
                enabled = enabled,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KlipprPurple,
                    disabledContainerColor = Color(0xFFCFC6E8),
                ),
                modifier = Modifier
                    .height(52.dp)
                    .width(180.dp),
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text("Generar QR", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DetailInfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = KlipprTextDark,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            color = KlipprTextDark,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp),
    ) {
        FloatingIconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = KlipprTextDark,
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(message, color = KlipprTextGray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun categoryLabel(category: PromotionCategory): String = when (category) {
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

private fun buildSubtitle(category: PromotionCategory, location: String?, description: String): String {
    return listOfNotNull(
        categoryLabel(category),
        location?.takeIf { it.isNotBlank() },
        description,
    ).joinToString(" · ")
}

internal fun redemptionsLabel(promotion: Promotion): String {
    if (promotion.availableRedemptions == Int.MAX_VALUE) return "Canjes ilimitados disponibles"
    val remaining = (promotion.availableRedemptions - promotion.currentRedemptions).coerceAtLeast(0)
    return when (remaining) {
        0 -> "Sin canjes disponibles"
        1 -> "1 canje disponible"
        else -> "$remaining canjes disponibles"
    }
}

private fun sharePromotion(context: Context, promotion: Promotion, businessDisplayName: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "$businessDisplayName\n${promotion.title}\n${promotion.description}",
        )
    }
    context.startActivity(Intent.createChooser(send, "Compartir"))
}
