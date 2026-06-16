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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.community.presentation.view.ReviewBottomSheet
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.promotions.domain.model.DiscountType
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPromosScreen(
    viewModel: RedemptionViewModel,
    communityViewModel: CommunityViewModel,
    currentUserId: String,
    onCodeClick: (String) -> Unit = {},
    onNavigateCommunity: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePromos: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val communityUiState by communityViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    // US-13: ReviewBottomSheet como modal sobre MisPromos
    if (communityUiState.isReviewSheetOpen) {
        ReviewBottomSheet(
            uiState = communityUiState,
            onDismiss = { communityViewModel.closeReviewSheet() },
            onRatingChanged = communityViewModel::onRatingChanged,
            onCommentChanged = communityViewModel::onCommentChanged,
            onSubmit = communityViewModel::submitReview
        )
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
                onNavigatePromos = onNavigatePromos,
            )
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = KlipprPurple) }

            state.error != null -> Box(
                Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                contentAlignment = Alignment.Center,
            ) { Text(state.error!!, color = TextSecondary) }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                redemptionSection(
                    title = "Códigos activos",
                    emptyText = "No tienes códigos activos",
                    codes = state.active,
                    onCodeClick = onCodeClick,
                    onLeaveReview = { code ->
                        communityViewModel.openReviewSheetForRedeemed(
                            code.promotionId,
                            code.promotionTitle ?: "Promoción"
                        )
                    },
                )
                redemptionSection(
                    title = "Historial usado",
                    emptyText = "Aún no tienes descuentos usados",
                    codes = state.redeemed,
                    onCodeClick = onCodeClick,
                    onLeaveReview = { code ->
                        communityViewModel.openReviewSheetForRedeemed(
                            code.promotionId,
                            code.promotionTitle ?: "Promoción"
                        )
                    },
                )
                redemptionSection(
                    title = "Expirados",
                    emptyText = "No tienes códigos expirados",
                    codes = state.expired,
                    onCodeClick = onCodeClick,
                    onLeaveReview = null,
                )
            }
        }
    }
}

private fun LazyListScope.redemptionSection(
    title: String,
    emptyText: String,
    codes: List<RedemptionCode>,
    onCodeClick: (String) -> Unit,
    onLeaveReview: ((RedemptionCode) -> Unit)?,
) {
    item(key = "${title}_header") {
        SectionHeader(title = title, count = codes.size)
    }
    if (codes.isEmpty()) {
        item(key = "${title}_empty") {
            EmptySectionMessage(emptyText)
        }
    } else {
        items(codes, key = { "${title}_${it.id}" }) { code ->
            RedemptionCard(
                code = code,
                onClick = { onCodeClick(code.id) },
                onLeaveReview = onLeaveReview?.let { cb -> { cb(code) } },
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Text(
        text = "$title ($count)",
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        color = TextPrimary,
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 2.dp),
    )
}

@Composable
private fun EmptySectionMessage(text: String) {
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

@Composable
private fun RedemptionCard(
    code: RedemptionCode,
    onClick: () -> Unit,
    onLeaveReview: (() -> Unit)?,
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
                    text = code.dateLabel(),
                    fontSize = 13.sp,
                    color = TextSecondary,
                )
            }
        }

        // US-13: botón "Dejar reseña" solo en canjes usados
        if (onLeaveReview != null) {
            TextButton(
                onClick = onLeaveReview,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = "Dejar reseña",
                    color = KlipprPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
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
    onNavigatePromos: () -> Unit,
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
            selected = true, onClick = {},
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos") },
            label = { Text("Favoritos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KlipprPurple,
                selectedTextColor = KlipprPurple,
                indicatorColor = KlipprLavender,
                unselectedIconColor = inactive,
                unselectedTextColor = inactive,
            ),
        )
        NavigationBarItem(
            selected = false, onClick = onNavigatePromos,
            icon = { Icon(Icons.Default.Apps, contentDescription = "Promos") },
            label = { Text("Promos", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactive, unselectedTextColor = inactive),
        )
    }
}

private fun RedemptionCode.discountLabel(): String {
    val value = discountValue ?: discountAppliedAmount
    return when (discountType) {
        DiscountType.FIXED_AMOUNT -> "S/ ${value.toInt()} OFF"
        else -> "${value.toInt()}% OFF"
    }
}

private fun RedemptionCode.dateLabel(): String = when (status) {
    RedemptionStatus.REDEEMED -> "Usado: ${formatVence(redeemedAt)}"
    RedemptionStatus.EXPIRED -> "Venció: ${formatVence(expiresAt)}"
    RedemptionStatus.ACTIVE -> "Vence: ${formatVence(expiresAt)}"
}
