package com.example.klippr.profile.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.presentation.state.ProfileStats
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.shared.presentation.components.KlipprTopBar
import com.example.klippr.shared.presentation.theme.KlipprPurple
import java.util.Locale

private val ScreenBg = Color(0xFFF7F7FB)
private val Ink = Color(0xFF17171F)
private val Muted = Color(0xFF666675)
private val Border = Color(0xFFE7E5F2)
private val SoftPurple = Color(0xFFF0EEFF)
private val Success = Color(0xFF168A4A)
private val Danger = Color(0xFFD32F2F)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { KlipprTopBar(title = "Mi perfil", onBack = onBack) },
        containerColor = ScreenBg,
        modifier = modifier,
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingProfile(Modifier.padding(innerPadding))
            state.error != null -> ErrorState(
                message = state.error.orEmpty(),
                onRetry = viewModel::load,
                modifier = Modifier.padding(innerPadding),
            )
            state.profile != null -> ProfileContent(
                profile = state.profile!!,
                stats = state.stats,
                profileSaveMessage = state.profileSaveMessage,
                onProfileSaveUnavailable = viewModel::markProfileSaveUnavailable,
                onLogout = onLogout,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    stats: ProfileStats,
    profileSaveMessage: String?,
    onProfileSaveUnavailable: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileHeader(profile = profile)
        StatsStrip(stats = stats)
        ProfileDetailsCard(
            profile = profile,
            profileSaveMessage = profileSaveMessage,
            onProfileSaveUnavailable = onProfileSaveUnavailable,
        )
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Danger),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
            Spacer(Modifier.size(8.dp))
            Text("Cerrar sesion", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ProfileHeader(profile: UserProfile) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(KlipprPurple),
                contentAlignment = Alignment.Center,
            ) {
                Text(profile.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.fullName, color = Ink, fontWeight = FontWeight.Bold, fontSize = 21.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(profile.email, color = Muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                StatusPill(if (profile.isActive) "Activo" else "Inactivo", profile.isActive)
            }
        }
    }
}

@Composable
private fun StatsStrip(stats: ProfileStats) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        StatTile("Canjes", stats.totalRedemptions.toString(), Icons.Default.LocalOffer, Modifier.weight(1f))
        StatTile("Activos", stats.activeRedemptions.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
        StatTile("Usados", stats.redeemedRedemptions.toString(), Icons.Default.Savings, Modifier.weight(1f))
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabelValue("Ahorro total", stats.totalSavings?.money() ?: "Sin datos")
            LabelValue("Promedio", stats.averageTransactionValue?.money() ?: "Sin datos", alignEnd = true)
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(92.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(icon, contentDescription = null, tint = KlipprPurple, modifier = Modifier.size(22.dp))
            Column {
                Text(value, color = Ink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(label, color = Muted, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ProfileDetailsCard(
    profile: UserProfile,
    profileSaveMessage: String?,
    onProfileSaveUnavailable: () -> Unit,
) {
    var editing by remember { mutableStateOf(false) }
    var firstName by remember(profile) { mutableStateOf(profile.firstName) }
    var lastName by remember(profile) { mutableStateOf(profile.lastName) }
    var phone by remember(profile) { mutableStateOf("") }
    var city by remember(profile) { mutableStateOf("") }
    var country by remember(profile) { mutableStateOf("") }

    SectionCard(
        title = "Datos personales",
        icon = Icons.Default.Person,
        action = {
            OutlinedButton(
                onClick = {
                    if (editing) {
                        firstName = profile.firstName
                        lastName = profile.lastName
                        phone = ""
                        city = ""
                        country = ""
                    }
                    editing = !editing
                },
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(if (editing) Icons.Default.Close else Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(if (editing) "Cancelar" else "Editar")
            }
        },
    ) {
        if (editing) {
            ProfileEditField("Nombre", firstName, Icons.Default.Person) { firstName = it }
            ProfileEditField("Apellido", lastName, Icons.Default.Person) { lastName = it }
            ProfileEditField("Telefono", phone, Icons.Default.Phone) { phone = it }
            ProfileEditField("Ciudad", city, Icons.Default.LocationOn) { city = it }
            ProfileEditField("Pais", country, Icons.Default.LocationOn) { country = it }
            Button(
                onClick = onProfileSaveUnavailable,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Guardar datos")
            }
            profileSaveMessage?.let { InlineNotice(it) }
        } else {
            InfoRow("Nombre", profile.firstName.ifBlank { "-" })
            InfoRow("Apellido", profile.lastName.ifBlank { "-" })
            InfoRow("Correo", profile.email, Icons.Default.Email)
            InfoRow("Rol", profile.role.ifBlank { "-" })
            InfoRow("Miembro desde", profile.memberSince.ifBlank { "-" })
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(icon, contentDescription = null, tint = KlipprPurple, modifier = Modifier.size(22.dp))
                Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                action?.invoke()
            }
            content()
        }
    }
}

@Composable
private fun ProfileEditField(label: String, value: String, icon: ImageVector, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    )
}

@Composable
private fun InfoRow(label: String, value: String, icon: ImageVector? = null) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(it, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
        }
        Text(label, fontSize = 14.sp, color = Muted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun LabelValue(label: String, value: String, alignEnd: Boolean = false) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(label, color = Muted, fontSize = 12.sp)
        Text(value, color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun StatusPill(text: String, positive: Boolean) {
    val bg = if (positive) Color(0xFFE8F7EF) else Color(0xFFFFECEB)
    val fg = if (positive) Success else Danger
    Text(
        text = text,
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

@Composable
private fun InlineNotice(message: String, isError: Boolean = false) {
    Text(
        text = message,
        color = if (isError) Danger else Muted,
        fontSize = 13.sp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (isError) Color(0xFFFFF0F0) else SoftPurple).padding(12.dp),
    )
}

@Composable
private fun LoadingProfile(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = KlipprPurple)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("No se pudo cargar tu perfil", color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text(message, color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = onRetry, shape = RoundedCornerShape(12.dp)) {
            Text("Reintentar")
        }
    }
}

private val UserProfile.initials: String
    get() = listOf(firstName, lastName)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { email.firstOrNull()?.uppercaseChar()?.toString().orEmpty() }

private fun Double.money(): String = "$" + String.format(Locale.US, "%.2f", this)
