package com.example.klippr.profile.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) = com.example.klippr.profile.presentation.views.ProfileScreen(
    viewModel = viewModel,
    onBack = onBack,
    onLogout = onLogout,
    modifier = modifier,
)