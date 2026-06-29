package com.example.klippr.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.profile.presentation.views.ProfileScreen

/** Grafo de navegacion del bounded context Profile. */
fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    onLogout: () -> Unit,
) {
    composable(ProfileRoutes.PROFILE) {
        ProfileScreen(
            viewModel = profileViewModel,
            onBack = { navController.popBackStack() },
            onLogout = onLogout,
        )
    }
}
