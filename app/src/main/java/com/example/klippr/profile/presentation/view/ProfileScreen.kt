package com.example.klippr.profile.presentation.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio

/** Pantalla de perfil (solo lectura). Muestra los datos de `GET /api/Users/{userId}` + logout. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KlipprPurple),
            )
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    color = KlipprPurple,
                    modifier = Modifier.padding(top = 64.dp),
                )

                state.error != null -> Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("😕", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { viewModel.load() }) { Text("Reintentar") }
                }

                state.profile != null -> ProfileContent(
                    profile = state.profile!!,
                    onLogout = onLogout,
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(profile: UserProfile, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        // Avatar placeholder (el backend no expone foto de usuario).
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(KlipprPurple),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.White, modifier = Modifier.size(64.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(profile.fullName, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1A1A1A))
        Text(profile.email, fontSize = 14.sp, color = Color(0xFF888888))

        Spacer(Modifier.height(24.dp))

        // Tarjeta de datos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            InfoRow("Nombre", profile.firstName.ifBlank { "—" })
            InfoRow("Apellido", profile.lastName.ifBlank { "—" })
            InfoRow("Correo", profile.email)
            InfoRow("Rol", profile.role.ifBlank { "—" })
            InfoRow("Estado", if (profile.isActive) "Activo" else "Inactivo")
            InfoRow("Miembro desde", profile.memberSince.ifBlank { "—" })
        }

        Spacer(Modifier.height(24.dp))

        // Editar perfil: deshabilitado por ahora (el backend no permite resolver el perfil editable).
        OutlinedButton(
            onClick = {},
            enabled = false,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Editar perfil (próximamente)")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = Color(0xFF888888))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
    }
}
