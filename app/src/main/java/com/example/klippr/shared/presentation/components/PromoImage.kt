package com.example.klippr.shared.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// @author Samuel Bonifacio
// Resuelve el imageKey del backend ("comida_pizza") a un drawable local con el mismo nombre.
// Devuelve 0 si no existe, para que la card caiga al placeholder/imagen remota sin crashear.
@Composable
fun rememberPromoDrawableId(imageKey: String?): Int {
    val ctx = LocalContext.current
    return remember(imageKey) {
        imageKey?.takeIf { it.isNotBlank() }
            ?.let { ctx.resources.getIdentifier(it, "drawable", ctx.packageName) }
            ?: 0
    }
}
