package com.example.klippr.shared.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.klippr.shared.presentation.theme.KlipprPurple

@Composable
fun FavoriteHeartButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedTint: Color = KlipprPurple,
    unselectedTint: Color = Color.LightGray,
    backgroundColor: Color = Color.Transparent,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor),
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Quitar favorito" else "Agregar favorito",
            tint = if (isFavorite) selectedTint else unselectedTint,
            modifier = Modifier.size(20.dp),
        )
    }
}
