package com.example.klippr.shared.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.shared.presentation.theme.KlipprLavender
import com.example.klippr.shared.presentation.theme.KlipprPurple

// @author Samuel Bonifacio

/** Pestañas del bottom nav compartido. */
enum class KlipprTab { COMUNIDAD, INICIO, FAVORITOS, PROMOS }

/**
 * Barra de navegación inferior compartida por las pantallas principales.
 * [current] marca la pestaña activa; cada callback navega a su destino.
 * "Comunidad" y "Favoritos" siguen siendo placeholders hasta tener pantalla propia.
 */
@Composable
fun KlipprBottomBar(
    current: KlipprTab,
    onComunidad: () -> Unit = {},
    onInicio: () -> Unit = {},
    onFavoritos: () -> Unit = {},
    onPromos: () -> Unit = {},
) {
    val inactive = Color(0xFF888888)
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = KlipprPurple,
        selectedTextColor = KlipprPurple,
        indicatorColor = KlipprLavender,
        unselectedIconColor = inactive,
        unselectedTextColor = inactive,
    )

    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavigationBarItem(
            selected = current == KlipprTab.INICIO, onClick = onInicio,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = current == KlipprTab.FAVORITOS, onClick = onFavoritos,
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos") },
            label = { Text("Favoritos", fontSize = 10.sp) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = current == KlipprTab.PROMOS, onClick = onPromos,
            icon = { Icon(Icons.Default.Apps, contentDescription = "Promos") },
            label = { Text("Promos", fontSize = 10.sp) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = current == KlipprTab.COMUNIDAD, onClick = onComunidad,
            icon = { Icon(Icons.Default.Group, contentDescription = "Comunidad") },
            label = { Text("Comunidad", fontSize = 10.sp) },
            colors = itemColors,
        )
    }
}
