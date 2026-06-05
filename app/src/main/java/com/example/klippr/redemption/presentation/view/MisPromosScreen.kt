package com.example.klippr.redemption.presentation.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.redemption.util.formatVence
import com.example.klippr.redemption.util.generateQrBitmap
import com.example.klippr.ui.theme.KlipprLavender
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio

private val TextSecondary = Color(0xFF888888)
private val TextPrimary = Color(0xFF1A1A1A)

/** Pestañas de "Mis Promos" mapeadas a estados de canje. */
private enum class PromosTab(val label: String, val status: RedemptionStatus) {
    ACTIVOS("Activos", RedemptionStatus.ACTIVE),
    CANJEADOS("Canjeados", RedemptionStatus.REDEEMED),
    EXPIRADOS("Expirados", RedemptionStatus.EXPIRED),
}

/**
 * "Mis Promos" (US-05 Activos · US-06 Canjeados · Expirados).
 * Carga el historial del consumidor y filtra por pestaña.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPromosScreen(
    viewModel: RedemptionViewModel,
    onCodeClick: (String) -> Unit = {},
    onNavigateCommunity: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(PromosTab.ACTIVOS) }

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    val codesForTab = when (selectedTab) {
        PromosTab.ACTIVOS -> state.active
        PromosTab.CANJEADOS -> state.redeemed
        PromosTab.EXPIRADOS -> state.expired
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Promos", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KlipprPurple),
            )
        },
        bottomBar = {
            MisPromosBottomBar(
                onNavigateCommunity = onNavigateCommunity,
                onNavigateHome = onNavigateHome,
            )
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PromosTabRow(
                selected = selectedTab,
                activeCount = state.active.size,
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

                codesForTab.isEmpty() -> Box(
                    Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No tienes códigos ${selectedTab.label.lowercase()}",
                        color = TextSecondary,
                        fontSize = 15.sp,
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(codesForTab, key = { it.id }) { code ->
                        RedemptionCard(code = code, onClick = { onCodeClick(code.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PromosTabRow(
    selected: PromosTab,
    activeCount: Int,
    onSelect: (PromosTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        PromosTab.entries.forEach { tab ->
            val isSelected = tab == selected
            val label = if (tab == PromosTab.ACTIVOS) "${tab.label} ($activeCount)" else tab.label
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    fontSize = 17.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) KlipprPurple else TextSecondary,
                    modifier = Modifier.clickable { onSelect(tab) },
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(if (isSelected) (label.length * 8).dp else 0.dp)
                        .background(if (isSelected) KlipprPurple else Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun RedemptionCard(code: RedemptionCode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Miniatura QR en tile blanco con sombra
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
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

            Spacer(Modifier.height(8.dp))
            Text(
                text = code.promotionTitle ?: code.discountLabel(),
                fontSize = 16.sp,
                color = TextPrimary,
            )

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Vence: ${formatVence(code.expiresAt)}",
                fontSize = 13.sp,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun StatusPill(status: RedemptionStatus) {
    val (text, bg, fg) = when (status) {
        RedemptionStatus.ACTIVE -> Triple("Activo", Color(0xFFB9F6CA), Color(0xFF1B7A3D))
        RedemptionStatus.REDEEMED -> Triple("Canjeado", KlipprLavender, KlipprPurple)
        RedemptionStatus.EXPIRED -> Triple("Expirado", Color(0xFFEEEEEE), TextSecondary)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 7.dp),
    ) {
        Text(text, color = fg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun MisPromosBottomBar(
    onNavigateCommunity: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    val inactive = TextSecondary
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavigationBarItem(
            selected = false, onClick = onNavigateCommunity,
            icon = { Icon(Icons.Default.Group, contentDescription = "Comunidad") },
            label = { Text("Comunidad", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactive, unselectedTextColor = inactive),
        )
        NavigationBarItem(
            selected = false, onClick = onNavigateHome,
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

// Etiqueta corta del descuento para la tarjeta cuando no hay título de promo.
private fun RedemptionCode.discountLabel(): String {
    val value = discountValue ?: discountAppliedAmount
    return when (discountType) {
        com.example.klippr.promotions.domain.model.DiscountType.FIXED_AMOUNT -> "S/ ${value.toInt()} OFF"
        else -> "${value.toInt()}% OFF"
    }
}
