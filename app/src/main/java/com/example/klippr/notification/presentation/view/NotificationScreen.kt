package com.example.klippr.notification.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.notification.domain.model.Notification
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.ui.theme.KlipprPurple
import com.example.klippr.ui.theme.KlipprTextDark
import com.example.klippr.ui.theme.KlipprTextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onBack: () -> Unit,
    onNotificationClick: (Notification) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (state.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Marcar todas", color = Color.White, fontSize = 13.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KlipprPurple,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = KlipprPurple)
                state.isEmpty -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("🔔", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Sin notificaciones", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = KlipprTextGray)
                    Spacer(Modifier.height(4.dp))
                    Text("Aquí verás tus avisos de canjes y favoritos", fontSize = 13.sp, color = Color.LightGray)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.notifications, key = { it.id }) { item ->
                        NotificationRow(
                            notification = item,
                            onClick = {
                                if (!item.isRead) viewModel.markAsRead(item.id)
                                onNotificationClick(item)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: Notification, onClick: () -> Unit) {
    val (icon, iconBg, iconTint) = when (notification.type) {
        NotificationType.REDEMPTION_GENERATED -> Triple(Icons.Filled.LocalOffer, Color(0xFFE4DCFB), KlipprPurple)
        NotificationType.REDEMPTION_EXPIRING -> Triple(Icons.Filled.Timer, Color(0xFFF8C0BC), Color(0xFFD3503F))
        NotificationType.FAVORITE_ADDED -> Triple(Icons.Filled.Favorite, Color(0xFFFBEFFA), KlipprPurple)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF6F3FF),
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(notification.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = KlipprTextDark)
                Spacer(Modifier.height(2.dp))
                Text(notification.message, fontSize = 12.sp, color = KlipprTextGray)
                Spacer(Modifier.height(4.dp))
                Text(timeAgo(notification.createdAt), fontSize = 11.sp, color = Color.LightGray)
            }
            if (!notification.isRead) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(KlipprPurple))
            }
        }
    }
}

private fun timeAgo(createdAt: Long): String {
    val minutes = (System.currentTimeMillis() - createdAt) / 60_000
    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace ${minutes}m"
        minutes < 1440 -> "Hace ${minutes / 60}h"
        else -> "Hace ${minutes / 1440}d"
    }
}