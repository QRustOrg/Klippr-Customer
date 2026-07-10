package com.example.klippr.shared.presentation.views.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.shared.presentation.components.KlipprTopBar
import com.example.klippr.shared.presentation.theme.KlipprPurple

// @author Samuel Bonifacio

/** Menú de ajustes (la "tuerca"). Hub de navegación a perfil, edición y cierre de sesión. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            KlipprTopBar(title = "Ajustes", onBack = onBack)
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Ver perfil",
                subtitle = "Consulta tus datos de cuenta",
                onClick = onNavigateToProfile,
            )
            SettingsItem(
                icon = Icons.Default.Edit,
                title = "Editar perfil",
                subtitle = "Próximamente",
                enabled = false,
                onClick = {},
            )
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Cerrar sesión",
                subtitle = "Salir de tu cuenta",
                tint = Color(0xFFD32F2F),
                onClick = onLogout,
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    tint: Color = KlipprPurple,
) {
    val contentColor = if (enabled) tint else Color(0xFFBDBDBD)
    val titleColor = if (enabled) Color(0xFF1A1A1A) else Color(0xFFBDBDBD)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = title, tint = contentColor, modifier = Modifier.size(26.dp))
        Spacer(Modifier.size(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = titleColor)
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF888888))
        }
        if (enabled) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFBDBDBD))
        }
    }
}
