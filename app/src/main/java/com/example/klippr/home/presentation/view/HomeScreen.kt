package com.example.klippr.home.presentation.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.R
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.shared.presentation.component.rememberPromoDrawableId
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.shared.presentation.component.DiscountBadge
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.shared.presentation.component.RemoteFavoriteHeartButton
import com.example.klippr.ui.theme.KlipprCardPink
import com.example.klippr.ui.theme.KlipprPurple
import com.example.klippr.ui.theme.KlipprTextDark
import com.example.klippr.ui.theme.KlipprTextGray

// @author Samuel Bonifacio

private val ScreenBg = Color(0xFFFFFFFF)
private val CardPink = KlipprCardPink
private val PurpleText = Color(0xFF8A6FE8)
private val TextDark = KlipprTextDark
private val TextGray = KlipprTextGray
private val PromoImgPlaceholder = Color(0xFFE4DCFB)
private val StarAmber = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profileViewModel: ProfileViewModel,
    promotionViewModel: PromotionViewModel,
    favoriteViewModel: FavoriteViewModel,
    redemptionViewModel: RedemptionViewModel,
    notificationViewModel: NotificationViewModel,
    currentUserId: String,
    onNavigateToSettings: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToMisPromos: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToQr: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    val promoState by promotionViewModel.listState.collectAsStateWithLifecycle()
    val favoriteState by favoriteViewModel.state.collectAsStateWithLifecycle()
    val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
    val notificationState by notificationViewModel.state.collectAsStateWithLifecycle()
    val favoriteByPromotion = remember(favoriteState.visibleFavorites) {
        favoriteState.visibleFavorites.associateBy { it.promotionId }
    }

    LaunchedEffect(currentUserId) {
        profileViewModel.load()
        redemptionViewModel.loadHistory()
        promotionViewModel.loadActive()
        favoriteViewModel.loadFavorites(currentUserId)
    }

    val greeting = profileState.profile?.greetingName ?: ""
    val activePromos = promoState.promotions.size
    val usedCoupons = redemptionState.redeemed.size
    val hasCoupons = redemptionState.active.isNotEmpty()

    var selectedPromotion by remember { mutableStateOf<Promotion?>(null) }

    LaunchedEffect(redemptionState.generated) {
        redemptionState.generated?.let { code ->
            selectedPromotion = null
            onNavigateToQr(code.id)
            redemptionViewModel.consumeGenerated()
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                name = greeting,
                onBell = onNavigateToNotifications,
                onSettings = onNavigateToSettings,
                unreadCount = notificationState.unreadCount,
            )
        },
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.INICIO,
                onComunidad = onNavigateToCommunity,
                onInicio = {},
                onFavoritos = onNavigateToMisPromos,
                onPromos = onNavigateToExplore,
            )
        },
        containerColor = ScreenBg,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            CouponsCard(
                hasCoupons = hasCoupons,
                count = redemptionState.active.size,
                onExplore = onNavigateToExplore,
                onSeeCoupons = onNavigateToMisPromos,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Promociones\nActivas",
                    value = activePromos.toString(),
                    icon = Icons.Default.Inbox,
                    iconBg = Color(0xFFB8F0C8),
                    iconTint = Color(0xFF1E9E54),
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Cupones\nUsados",
                    value = usedCoupons.toString(),
                    icon = Icons.Default.ContentCut,
                    iconBg = Color(0xFFF8C0BC),
                    iconTint = Color(0xFFD3503F),
                )
            }
            Spacer(Modifier.height(24.dp))
            Text("Tiendas Populares Cerca", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PurpleText)
            Spacer(Modifier.height(14.dp))
            StorePillsRow(onClick = onNavigateToExplore)
            Spacer(Modifier.height(24.dp))
            when {
                promoState.isLoading && promoState.promotions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = KlipprPurple) }
                }
                promoState.promotions.isEmpty() -> {
                    Text(
                        "No hay promociones activas",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
                else -> {
                    val grouped = promoState.promotions.groupBy { it.category }
                    PromotionCategory.entries.filter { grouped.containsKey(it) }.forEach { category ->
                        PromoCategorySection(
                            title = category.label(),
                            promotions = grouped[category].orEmpty(),
                            favoriteByPromotionId = favoriteByPromotion,
                            favoriteViewModel = favoriteViewModel,
                            currentUserId = currentUserId,
                            onPromotionClick = { promo -> selectedPromotion = promo },
                            onFavoriteSaved = { promo, wasFavorite ->
                                promotionViewModel.toggleFavorite(promo.id, true)
                                if (!wasFavorite) {
                                    notificationViewModel.notify(
                                        type = NotificationType.FAVORITE_ADDED,
                                        title = "Guardado en favoritos",
                                        message = "Agregaste una promo a tus favoritos.",
                                        relatedId = promo.id,
                                    )
                                }
                            },
                            onSeeMore = onNavigateToExplore,
                        )
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    selectedPromotion?.let { promo ->
        PromoApplyModal(
            promotion = promo,
            isLoading = redemptionState.isGenerating,
            error = redemptionState.error,
            onDismiss = {
                selectedPromotion = null
                redemptionViewModel.consumeError()
            },
            onApply = { redemptionViewModel.generate(promo) },
        )
    }
}

@Composable
private fun PromoCategorySection(
    title: String,
    promotions: List<Promotion>,
    favoriteByPromotionId: Map<String, com.example.klippr.favorites.domain.model.Favorite>,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onPromotionClick: (Promotion) -> Unit,
    onFavoriteSaved: (Promotion, Boolean) -> Unit,
    onSeeMore: () -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSeeMore)
            .padding(bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextDark,
            modifier = Modifier.weight(1f),
        )
        Icon(Icons.Default.ChevronRight, contentDescription = "Ver más", tint = TextDark, modifier = Modifier.size(26.dp))
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(promotions, key = { it.id }) { promo ->
            val isFavorite = favoriteByPromotionId.containsKey(promo.id)
            PromoCardVertical(
                promotion = promo,
                isFavorite = isFavorite,
                favoriteViewModel = favoriteViewModel,
                currentUserId = currentUserId,
                onClick = { onPromotionClick(promo) },
                onFavoriteSaved = { onFavoriteSaved(promo, isFavorite) },
                onShareClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${promo.title}\n${promo.description}")
                    }
                    context.startActivity(Intent.createChooser(send, "Compartir"))
                },
            )
        }
    }
}

@Composable
private fun PromoCardVertical(
    promotion: Promotion,
    isFavorite: Boolean,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onClick: () -> Unit,
    onFavoriteSaved: () -> Unit,
    onShareClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .width(230.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.05f)
                .clip(RoundedCornerShape(16.dp))
                .background(PromoImgPlaceholder),
        ) {
            val resId = rememberPromoDrawableId(promotion.imageKey)
            if (resId != 0) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = promotion.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            RemoteFavoriteHeartButton(
                userId = currentUserId,
                promotionId = promotion.id,
                isFavorite = isFavorite,
                favoriteViewModel = favoriteViewModel,
                selectedTint = Color.White,
                unselectedTint = Color.White,
                backgroundColor = Color.Black.copy(alpha = 0.28f),
                modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                onSaved = onFavoriteSaved,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.28f))
                    .clickable(onClick = onShareClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        promotion.businessName?.takeIf { it.isNotBlank() }?.let { name ->
            Text(
                text = name,
                fontSize = 12.sp,
                color = TextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
        }
        Text(
            text = promotion.title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            DiscountBadge(promotion.discountType, promotion.discountValue)
            promotion.rating?.let { r ->
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Star, contentDescription = null, tint = StarAmber, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(2.dp))
                Text("%.1f".format(r), fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromoApplyModal(
    promotion: Promotion,
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            val resId = rememberPromoDrawableId(promotion.imageKey)
            if (resId != 0) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = promotion.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )
                Spacer(Modifier.height(16.dp))
            }
            Text(
                text = promotion.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextDark,
            )
            Spacer(Modifier.height(8.dp))
            DiscountBadge(promotion.discountType, promotion.discountValue)
            Spacer(Modifier.height(10.dp))
            Text(
                text = promotion.description,
                fontSize = 14.sp,
                color = TextGray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(text = error, color = Color.Red, fontSize = 13.sp)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onApply,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                    )
                } else {
                    Text("Aplicar Descuento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

private fun PromotionCategory.label(): String = when (this) {
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

@Composable
private fun HomeTopBar(name: String, onBell: () -> Unit, onSettings: () -> Unit, unreadCount: Int = 0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KlipprPurple)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.klippr_lockup),
                contentDescription = "Klippr",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(4.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (name.isBlank()) "Hola!" else "Hola, $name!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.weight(1f),
        )
        Box {
        IconButton(onClick = onBell) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = Color.White
            )
        }
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(8.dp)
                    .background(Color(0xFFE53935), CircleShape),
            )
        }
    }

        IconButton(onClick = onSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun CouponsCard(hasCoupons: Boolean, count: Int, onExplore: () -> Unit, onSeeCoupons: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardPink)
            .clickable(onClick = onSeeCoupons)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("¡Tus Cupones!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PurpleText)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFFD8C7E8), modifier = Modifier.size(40.dp))
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = "Cupón", tint = Color(0xFFCBCBCB), modifier = Modifier.size(96.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFB79CE0), modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(12.dp))
        if (hasCoupons) {
            Text("Tienes $count cupon${if (count == 1) "" else "es"}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = PurpleText)
            Spacer(Modifier.height(4.dp))
            Text("Toca 'Favoritos' para verlos", fontSize = 13.sp, color = PurpleText, fontWeight = FontWeight.SemiBold)
        } else {
            Text("Aun no hay Cupones", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = PurpleText)
            Spacer(Modifier.height(4.dp))
            Text(
                "Explora la app para conseguir más",
                fontSize = 13.sp,
                color = PurpleText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onExplore),
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PurpleText, lineHeight = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
    }
}

private data class StoreCategory(val emoji: String, val label: String, val bg: Color)

@Composable
private fun StorePillsRow(onClick: () -> Unit) {
    val categories = listOf(
        StoreCategory("🍔", "Fast Food", Color(0xFFF6CC8A)),
        StoreCategory("🎬", "Entretenimiento", Color(0xFFC9A27E)),
        StoreCategory("💊", "Salud", Color(0xFFA9D4F5)),
        StoreCategory("👗", "Moda", Color(0xFFB6E5C0)),
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(categories.size) { i ->
            val c = categories[i]
            Column(
                modifier = Modifier
                    .width(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable(onClick = onClick)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(58.dp).clip(CircleShape).background(c.bg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(c.emoji, fontSize = 28.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(c.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PurpleText)
            }
        }
    }
}
