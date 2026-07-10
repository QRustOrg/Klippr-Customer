package com.example.klippr.settings.presentation.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.preferences.domain.model.UserPreference
import com.example.klippr.preferences.presentation.viewmodel.PreferenceViewModel
import com.example.klippr.shared.presentation.components.KlipprBottomBar
import com.example.klippr.shared.presentation.components.KlipprTab
import com.example.klippr.shared.presentation.components.KlipprTopBar
import com.example.klippr.shared.presentation.theme.KlipprPurple

private val ScreenBg = Color.White
private val Ink = Color(0xFF202027)
private val Muted = Color(0xFF777780)
private val Divider = Color(0xFFE3E3E6)
private val Danger = Color(0xFFD32F2F)
private val SoftPurple = Color(0xFFF1EFFF)

private const val SectionPersonal = "personal"
private const val SectionSecurity = "security"
private const val SectionPrivacy = "privacy"
private const val SectionNotifications = "notifications"
private const val SectionPayments = "payments"
private const val SectionPreferences = "preferences"
private const val SectionBenefits = "benefits"
private const val SectionAccessibility = "accessibility"
private const val SectionHelp = "help"

enum class SettingsSectionKind {
    PersonalInfo,
    Placeholder,
    Privacy,
    Notifications,
    Preferences,
}

data class SettingsSection(
    val routeKey: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val kind: SettingsSectionKind,
)

data class SettingsDetailRow(
    val title: String,
    val value: String? = null,
    val checked: Boolean? = null,
)

fun accountSettingsSections(): List<SettingsSection> = listOf(
    SettingsSection(
        routeKey = SectionPersonal,
        title = "Información personal",
        description = "Datos de perfil, correo y estado de cuenta.",
        icon = Icons.Default.Person,
        kind = SettingsSectionKind.PersonalInfo,
    ),
    SettingsSection(
        routeKey = SectionSecurity,
        title = "Login y seguridad",
        description = "Contraseña, sesiones y acceso seguro.",
        icon = Icons.Default.Security,
        kind = SettingsSectionKind.Placeholder,
    ),
    SettingsSection(
        routeKey = SectionPrivacy,
        title = "Privacidad",
        description = "Controla visibilidad y uso de datos.",
        icon = Icons.Default.PrivacyTip,
        kind = SettingsSectionKind.Privacy,
    ),
    SettingsSection(
        routeKey = SectionNotifications,
        title = "Notificaciones",
        description = "Elige cómo quieres recibir avisos.",
        icon = Icons.Default.Notifications,
        kind = SettingsSectionKind.Notifications,
    ),
    SettingsSection(
        routeKey = SectionPayments,
        title = "Pagos",
        description = "Métodos de pago y comprobantes.",
        icon = Icons.Default.Payment,
        kind = SettingsSectionKind.Placeholder,
    ),
    SettingsSection(
        routeKey = SectionPreferences,
        title = "Preferencias",
        description = "Idioma, zona horaria y experiencia.",
        icon = Icons.Default.Language,
        kind = SettingsSectionKind.Preferences,
    ),
    SettingsSection(
        routeKey = SectionBenefits,
        title = "Beneficios Klippr",
        description = "Promos, niveles y recompensas.",
        icon = Icons.Default.WorkspacePremium,
        kind = SettingsSectionKind.Placeholder,
    ),
    SettingsSection(
        routeKey = SectionAccessibility,
        title = "Accesibilidad",
        description = "Opciones para mejorar el uso de la app.",
        icon = Icons.Default.Accessibility,
        kind = SettingsSectionKind.Placeholder,
    ),
    SettingsSection(
        routeKey = SectionHelp,
        title = "Ayuda",
        description = "Soporte, preguntas frecuentes y contacto.",
        icon = Icons.AutoMirrored.Filled.HelpOutline,
        kind = SettingsSectionKind.Placeholder,
    ),
)

fun preferenceDetailRows(preference: UserPreference): List<SettingsDetailRow> = listOf(
    SettingsDetailRow("Idioma", languageLabel(preference.languageCode)),
    SettingsDetailRow("Zona horaria", preference.timezone),
    SettingsDetailRow("Modo oscuro", checked = preference.darkMode),
)

fun notificationDetailRows(preference: UserPreference): List<SettingsDetailRow> = listOf(
    SettingsDetailRow("Notificaciones por correo", checked = preference.emailNotifications),
    SettingsDetailRow("Notificaciones push", checked = preference.pushNotifications),
    SettingsDetailRow("Notificaciones SMS", checked = preference.smsNotifications),
)

fun privacyDetailRows(preference: UserPreference): List<SettingsDetailRow> = listOf(
    SettingsDetailRow("Perfil público", checked = preference.profileVisibility == "public"),
    SettingsDetailRow("Compartir datos para personalización", checked = preference.dataSharingConsent),
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigatePromos: () -> Unit,
    onNavigateCommunity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    SettingsScaffold(
        title = "Ajustes",
        onBack = onBack,
        onNavigateHome = onNavigateHome,
        onNavigateFavorites = onNavigateFavorites,
        onNavigatePromos = onNavigatePromos,
        onNavigateCommunity = onNavigateCommunity,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
        ) {
            Text(
                text = "Ajustes de cuenta",
                color = Ink,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 34.sp,
            )
            Spacer(Modifier.height(24.dp))
            accountSettingsSections().forEach { section ->
                SettingsHubRow(
                    section = section,
                    onClick = {
                        if (section.kind == SettingsSectionKind.PersonalInfo) {
                            onNavigateToProfile()
                        } else {
                            onNavigateToDetail(section.routeKey)
                        }
                    },
                )
            }
            HorizontalDivider(color = Divider, modifier = Modifier.padding(top = 30.dp, bottom = 24.dp))
            Text(appVersionLabel(context), color = Muted, fontSize = 15.sp)
            Spacer(Modifier.height(16.dp))
            LogoutRow(onClick = { showLogoutDialog = true })
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false },
        )
    }
}

@Composable
fun SettingsDetailScreen(
    sectionKey: String,
    viewModel: PreferenceViewModel,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigatePromos: () -> Unit,
    onNavigateCommunity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val section = accountSettingsSections().firstOrNull { it.routeKey == sectionKey }

    SettingsScaffold(
        title = section?.title ?: "Ajustes",
        onBack = onBack,
        onNavigateHome = onNavigateHome,
        onNavigateFavorites = onNavigateFavorites,
        onNavigatePromos = onNavigatePromos,
        onNavigateCommunity = onNavigateCommunity,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            if (section == null) {
                DetailHeader(
                    icon = Icons.Default.Settings,
                    title = "Ajuste no disponible",
                    description = "No encontramos esta sección de ajustes.",
                )
                return@Column
            }
            DetailHeader(section.icon, section.title, section.description)
            when (section.kind) {
                SettingsSectionKind.Privacy -> PreferenceContent(
                    preference = state.preference,
                    error = state.error,
                    saveMessage = state.saveMessage,
                    isSaving = state.isSaving,
                    onSave = viewModel::savePreference,
                    content = { current, onDraftChange ->
                        ToggleSettingRow(
                            icon = Icons.Default.Person,
                            title = "Perfil público",
                            description = "Permite que otros usuarios vean información básica de tu perfil.",
                            checked = current.profileVisibility == "public",
                            onCheckedChange = {
                                onDraftChange(current.copy(profileVisibility = if (it) "public" else "private"))
                            },
                        )
                        ToggleSettingRow(
                            icon = Icons.Default.ToggleOn,
                            title = "Compartir datos para personalización",
                            description = "Ayuda a Klippr a recomendar promociones más relevantes para ti.",
                            checked = current.dataSharingConsent,
                            onCheckedChange = { onDraftChange(current.copy(dataSharingConsent = it)) },
                        )
                    },
                )
                SettingsSectionKind.Notifications -> PreferenceContent(
                    preference = state.preference,
                    error = state.error,
                    saveMessage = state.saveMessage,
                    isSaving = state.isSaving,
                    onSave = viewModel::savePreference,
                    content = { current, onDraftChange ->
                        ToggleSettingRow(
                            icon = Icons.Default.Email,
                            title = "Notificaciones por correo",
                            description = "Recibe novedades, canjes y avisos importantes por email.",
                            checked = current.emailNotifications,
                            onCheckedChange = { onDraftChange(current.copy(emailNotifications = it)) },
                        )
                        ToggleSettingRow(
                            icon = Icons.Default.Notifications,
                            title = "Notificaciones push",
                            description = "Activa alertas dentro del dispositivo.",
                            checked = current.pushNotifications,
                            onCheckedChange = { onDraftChange(current.copy(pushNotifications = it)) },
                        )
                        ToggleSettingRow(
                            icon = Icons.Default.Sms,
                            title = "Notificaciones SMS",
                            description = "Recibe avisos urgentes por mensaje de texto.",
                            checked = current.smsNotifications,
                            onCheckedChange = { onDraftChange(current.copy(smsNotifications = it)) },
                        )
                    },
                )
                SettingsSectionKind.Preferences -> PreferenceContent(
                    preference = state.preference,
                    error = state.error,
                    saveMessage = state.saveMessage,
                    isSaving = state.isSaving,
                    onSave = viewModel::savePreference,
                    content = { current, onDraftChange ->
                        ChoiceSettingRow(
                            icon = Icons.Default.Language,
                            title = "Idioma",
                            description = "Define el idioma principal de la experiencia.",
                            options = listOf("es" to "Español", "en" to "English"),
                            selected = current.languageCode,
                            onSelect = { onDraftChange(current.copy(languageCode = it)) },
                        )
                        ChoiceSettingRow(
                            icon = Icons.Default.Schedule,
                            title = "Zona horaria",
                            description = "Usada para vencimientos y recordatorios.",
                            options = listOf(
                                "America/Lima" to "Lima",
                                "America/Bogota" to "Bogotá",
                                "America/Mexico_City" to "Ciudad de México",
                            ),
                            selected = current.timezone,
                            onSelect = { onDraftChange(current.copy(timezone = it)) },
                        )
                        ToggleSettingRow(
                            icon = Icons.Default.ToggleOn,
                            title = "Modo oscuro",
                            description = "Guarda tu preferencia de tema para futuras sesiones.",
                            checked = current.darkMode,
                            onCheckedChange = { onDraftChange(current.copy(darkMode = it)) },
                        )
                    },
                )
                SettingsSectionKind.Placeholder -> PlaceholderContent(section)
                SettingsSectionKind.PersonalInfo -> PlaceholderContent(section)
            }
        }
    }
}

@Composable
private fun SettingsScaffold(
    title: String,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigatePromos: () -> Unit,
    onNavigateCommunity: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = { KlipprTopBar(title = title, onBack = onBack) },
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.INICIO,
                onInicio = onNavigateHome,
                onFavoritos = onNavigateFavorites,
                onPromos = onNavigatePromos,
                onComunidad = onNavigateCommunity,
            )
        },
        containerColor = ScreenBg,
        modifier = modifier,
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun SettingsHubRow(section: SettingsSection, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(section.icon, contentDescription = null, tint = Ink, modifier = Modifier.size(31.dp))
        Spacer(Modifier.size(24.dp))
        Text(
            text = section.title,
            color = Ink,
            fontSize = 21.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9A9A9D), modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun DetailHeader(icon: ImageVector, title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(SoftPurple),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = KlipprPurple, modifier = Modifier.size(30.dp))
        }
        Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 36.sp)
        Text(description, color = Muted, fontSize = 15.sp, lineHeight = 21.sp)
    }
}

@Composable
private fun PreferenceContent(
    preference: UserPreference?,
    error: String?,
    saveMessage: String?,
    isSaving: Boolean,
    onSave: (UserPreference) -> Unit,
    content: @Composable ColumnScope.(UserPreference, (UserPreference) -> Unit) -> Unit,
) {
    if (preference == null) {
        if (error == null) {
            CircularProgressIndicator(color = KlipprPurple)
        } else {
            Notice(error)
        }
        return
    }

    var draft by remember(preference) { mutableStateOf(preference) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        content(draft) { draft = it }
        Button(
            enabled = draft != preference && !isSaving,
            onClick = { onSave(draft) },
            colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple, disabledContainerColor = Color(0xFFD7D2FA)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar cambios", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        saveMessage?.let { SuccessNotice(it) }
        error?.let { Notice(it) }
    }
}

@Composable
private fun ToggleSettingRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F8FA))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(26.dp))
        Spacer(Modifier.size(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(description, color = Muted, fontSize = 13.sp, lineHeight = 18.sp)
        }
        Spacer(Modifier.size(10.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ChoiceSettingRow(
    icon: ImageVector,
    title: String,
    description: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(26.dp))
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(description, color = Muted, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (value, label) ->
                FilterChip(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderContent(section: SettingsSection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF8F8FA))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Próximamente", color = KlipprPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
            text = "Estamos preparando ${section.title.lowercase()} para que puedas gestionarlo desde Klippr sin salir de la app.",
            color = Ink,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun Notice(message: String) {
    Text(
        text = message,
        color = Danger,
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFECEB))
            .padding(12.dp),
    )
}

@Composable
private fun SuccessNotice(message: String) {
    Text(
        text = message,
        color = Color(0xFF168A4A),
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F7EF))
            .padding(12.dp),
    )
}

@Composable
private fun LogoutRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Danger, modifier = Modifier.size(27.dp))
        Spacer(Modifier.size(18.dp))
        Text("Cerrar sesión", color = Danger, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    }
}

@Composable
private fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
        text = { Text("¿Quieres salir de tu cuenta de Klippr?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Cerrar sesión", color = Danger, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = KlipprPurple, fontWeight = FontWeight.SemiBold)
            }
        },
    )
}

private fun languageLabel(code: String): String = when (code) {
    "en" -> "English"
    else -> "Español"
}

@Suppress("DEPRECATION")
private fun appVersionLabel(context: Context): String {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    return "Versión ${packageInfo.versionName ?: "1.0"} (${packageInfo.versionCode})"
}
