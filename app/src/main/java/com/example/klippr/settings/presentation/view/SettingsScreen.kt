package com.example.klippr.settings.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.profile.domain.model.UserPreference
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus
import com.example.klippr.shared.presentation.component.rememberPromoDrawableId
import com.example.klippr.ui.theme.KlipprPurple

private val Bg = Color(0xFFF6F6F8)
private val Ink = Color(0xFF17171F)
private val Muted = Color(0xFF666675)
private val Line = Color(0xFFE2E2E8)
private val Danger = Color(0xFFE53935)
private val SoftPurple = Color(0xFFEDEBFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLogout by remember { mutableStateOf(false) }

    Scaffold(containerColor = Bg, modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Header()
            AccountMenu(
                onNavigateToProfile = onNavigateToProfile,
                onLogoutClick = { showLogout = true },
            )
            PreferencesSection(
                preference = state.preference,
                error = state.preferenceError,
                isSaving = state.isSavingPreference,
                onSave = viewModel::savePreference,
            )
            ActivitySection(
                redemptions = state.latestRedemptions,
                error = state.activityError,
            )
        }
    }

    if (showLogout) {
        ModalBottomSheet(
            onDismissRequest = { showLogout = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.Transparent,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = {
                        showLogout = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Danger),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                ) { Text("Cerrar sesion", fontSize = 18.sp) }
                Button(
                    onClick = { showLogout = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = KlipprPurple),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                ) { Text("Cancelar", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Ajustes", color = Ink, fontWeight = FontWeight.Bold, fontSize = 34.sp)
        Icon(
            Icons.Default.Notifications,
            contentDescription = "Notificaciones",
            tint = Ink,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAEAEE))
                .padding(15.dp),
        )
    }
}

@Composable
private fun AccountMenu(onNavigateToProfile: () -> Unit, onLogoutClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SettingsRow(Icons.Default.Settings, "Configuracion de cuenta", onNavigateToProfile)
        SettingsRow(Icons.AutoMirrored.Filled.HelpOutline, "Ayuda", {})
        SettingsRow(Icons.Default.Person, "Ver perfil", onNavigateToProfile)
        SettingsRow(Icons.Default.PrivacyTip, "Privacidad", {})
        HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 12.dp))
        SettingsRow(Icons.Default.WorkspacePremium, "Beneficios Klippr", {})
        SettingsRow(Icons.AutoMirrored.Filled.Logout, "Cerrar sesion", onLogoutClick, showChevron = false)
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, onClick: () -> Unit, showChevron: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(30.dp))
        Spacer(Modifier.size(22.dp))
        Text(title, color = Ink, fontSize = 21.sp, modifier = Modifier.weight(1f))
        if (showChevron) Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted)
    }
}

@Composable
private fun PreferencesSection(
    preference: UserPreference?,
    error: String?,
    isSaving: Boolean,
    onSave: (UserPreference) -> Unit,
) {
    var draft by remember(preference) { mutableStateOf(preference) }
    val current = draft

    Section(title = "Preferencias", icon = Icons.Default.Security) {
        if (preference == null || current == null) {
            Notice(error ?: "No se pudieron cargar las preferencias")
            return@Section
        }
        ChoiceRow(
            "Idioma",
            Icons.Default.Language,
            listOf("es" to "Espanol", "en" to "English"),
            current.languageCode,
        ) { draft = current.copy(languageCode = it) }
        ChoiceRow(
            "Privacidad",
            Icons.Default.PrivacyTip,
            listOf("private" to "Privado", "public" to "Publico"),
            current.profileVisibility,
        ) { draft = current.copy(profileVisibility = it) }
        ToggleRow("Email", current.emailNotifications) { draft = current.copy(emailNotifications = it) }
        ToggleRow("Push", current.pushNotifications) { draft = current.copy(pushNotifications = it) }
        ToggleRow("SMS", current.smsNotifications) { draft = current.copy(smsNotifications = it) }
        TextButton(
            enabled = current != preference && !isSaving,
            onClick = { onSave(current) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar")
        }
    }
}

@Composable
private fun ActivitySection(redemptions: List<RedemptionCode>, error: String?) {
    Section(title = "Actividad reciente", icon = Icons.Default.Tag) {
        if (error != null) {
            Notice(error)
            return@Section
        }
        if (redemptions.isEmpty()) {
            Text("Tus ultimos canjes apareceran aqui.", color = Muted, fontSize = 14.sp)
            return@Section
        }
        redemptions.forEachIndexed { index, code ->
            ActivityRow(code)
            if (index != redemptions.lastIndex) HorizontalDivider(color = Line)
        }
    }
}

@Composable
private fun ActivityRow(code: RedemptionCode) {
    val drawableId = rememberPromoDrawableId(code.imageKey)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (drawableId != 0) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(46.dp).clip(CircleShape),
            )
        } else {
            Icon(
                Icons.Default.LocalOffer,
                contentDescription = null,
                tint = KlipprPurple,
                modifier = Modifier.size(46.dp).clip(CircleShape).background(SoftPurple).padding(12.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(code.promotionTitle ?: "Promocion", color = Ink, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(code.businessName ?: code.promotionId, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        StatusPill(code.status)
    }
}

@Composable
private fun Section(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = KlipprPurple, modifier = Modifier.size(22.dp))
            Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceRow(
    title: String,
    icon: ImageVector,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
            Text(title, color = Ink, fontWeight = FontWeight.SemiBold)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (value, label) ->
                FilterChip(selected = selected == value, onClick = { onSelect(value) }, label = { Text(label) })
            }
        }
    }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Ink, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun StatusPill(status: RedemptionStatus) {
    val text = when (status) {
        RedemptionStatus.ACTIVE -> "Activo"
        RedemptionStatus.REDEEMED -> "Usado"
        RedemptionStatus.EXPIRED -> "Vencido"
    }
    val positive = status == RedemptionStatus.ACTIVE
    Text(
        text,
        color = if (positive) Color(0xFF168A4A) else Danger,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(if (positive) Color(0xFFE8F7EF) else Color(0xFFFFECEB)).padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun Notice(message: String) {
    Text(
        message,
        color = Danger,
        fontSize = 13.sp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFFFECEB)).padding(12.dp),
    )
}
