package com.example.klippr.home.presentation.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio

private val ScreenBg = Color(0xFFFFFFFF)
private val CardPink = Color(0xFFFBEFFA)
private val PurpleText = Color(0xFF8A6FE8)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF888888)

/**
 * Pantalla principal (Home/Inicio). Reúne saludo (perfil), cupones (redenciones),
 * estadísticas y accesos rápidos. La tuerca abre Settings; la campana es placeholder.
 */
@Composable
fun HomeScreen(
    profileViewModel: ProfileViewModel,
    promotionViewModel: PromotionViewModel,
    redemptionViewModel: RedemptionViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToMisPromos: () -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    val promoState by promotionViewModel.listState.collectAsStateWithLifecycle()
    val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()

    // Recarga al entrar al Home (la sesión ya existe tras el login).
    LaunchedEffect(Unit) {
        profileViewModel.load()
        redemptionViewModel.loadHistory()
    }

    val greeting = profileState.profile?.greetingName ?: ""
    val activePromos = promoState.promotions.size
    val usedCoupons = redemptionState.redeemed.size
    val hasCoupons = redemptionState.active.isNotEmpty()

    Scaffold(
        topBar = {
            HomeTopBar(
                name = greeting,
                onBell = { /* placeholder: sin backend de notificaciones aún */ },
                onSettings = onNavigateToSettings,
            )
        },
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.INICIO,
                onComunidad = onNavigateToCreate,
                onInicio = { /* ya estamos en Home */ },
                onFavoritos = { /* placeholder */ },
                onPromos = onNavigateToMisPromos,
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
            CouponsCard(hasCoupons = hasCoupons, count = redemptionState.active.size, onExplore = onNavigateToExplore)

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
            Text("PARA TI", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PurpleText)
            Spacer(Modifier.height(12.dp))
            ForYouRow(onClick = onNavigateToExplore)

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HomeTopBar(name: String, onBell: () -> Unit, onSettings: () -> Unit) {
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
                .background(Color(0xFFFFC93C)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.White, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (name.isBlank()) "Hola!" else "Hola, $name!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onBell) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White, modifier = Modifier.size(26.dp))
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun CouponsCard(hasCoupons: Boolean, count: Int, onExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardPink)
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
            Text("Tienes $count cupón${if (count == 1) "" else "es"}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = PurpleText)
            Spacer(Modifier.height(4.dp))
            Text("Toca 'Promos' para verlos", fontSize = 13.sp, color = PurpleText, fontWeight = FontWeight.SemiBold)
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
        StoreCategory("☕", "Cafeterias", Color(0xFFC9A27E)),
        StoreCategory("🍔", "Fast Food", Color(0xFFF6CC8A)),
        StoreCategory("🛒", "Productos", Color(0xFFA9D4F5)),
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

@Composable
private fun ForYouRow(onClick: () -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(3) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF6F0FF))
                    .clickable(onClick = onClick)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text("Descubre promos\npara ti", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = PurpleText)
            }
        }
    }
}
