package com.example.klippr.shared.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.ui.theme.KlipprPurple

@Composable
fun RemoteFavoriteHeartButton(
    userId: String,
    promotionId: String,
    isFavorite: Boolean,
    favoriteViewModel: FavoriteViewModel,
    modifier: Modifier = Modifier,
    selectedTint: Color = KlipprPurple,
    unselectedTint: Color = Color.LightGray,
    backgroundColor: Color = Color.Transparent,
    onSaved: () -> Unit = {},
) {
    FavoriteHeartButton(
        isFavorite = isFavorite,
        onClick = {
            if (userId.isNotBlank() && promotionId.isNotBlank()) {
                favoriteViewModel.addFavorite(userId, promotionId, onSaved)
            }
        },
        modifier = modifier,
        selectedTint = selectedTint,
        unselectedTint = unselectedTint,
        backgroundColor = backgroundColor,
    )
}
