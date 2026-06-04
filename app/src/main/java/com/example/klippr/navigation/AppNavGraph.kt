package com.example.klippr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.klippr.promotions.presentation.view.CreatePromotionScreen
import com.example.klippr.promotions.presentation.view.ExploreScreen
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel

// @author Samuel Bonifacio

@Composable
fun AppNavGraph(
    viewModel: PromotionViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Routes.EXPLORE) {
        composable(Routes.EXPLORE) {
            ExploreScreen(
                viewModel = viewModel,
                onPromotionClick = { /* TODO: navegar a detalle */ },
                onBack = { /* pantalla raíz */ },
                onNavigateToCreate = { navController.navigate(Routes.CREATE_PROMOTION) },
            )
        }
        composable(Routes.CREATE_PROMOTION) {
            CreatePromotionScreen(
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.EXPLORE) {
                        popUpTo(Routes.EXPLORE) { inclusive = true }
                    }
                },
            )
        }
    }
}
