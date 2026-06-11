package com.example.klippr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.klippr.home.presentation.view.HomeScreen
import com.example.klippr.iam.presentation.view.ForgotPasswordScreen
import com.example.klippr.iam.presentation.view.SignInScreen
import com.example.klippr.iam.presentation.view.SignUpScreen
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.profile.presentation.view.ProfileScreen
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.presentation.view.CreatePromotionScreen
import com.example.klippr.promotions.presentation.view.ExploreScreen
import com.example.klippr.promotions.presentation.view.PromotionDetailScreen
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.view.MisPromosScreen
import com.example.klippr.redemption.presentation.view.QrCodeScreen
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.settings.presentation.view.SettingsScreen

// @author Samuel Bonifacio

@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    viewModel: PromotionViewModel,
    redemptionViewModel: RedemptionViewModel,
    navController: NavHostController = rememberNavController(),
) {
    // Cierra sesión y vuelve al login limpiando todo el backstack.
    val logout: () -> Unit = {
        authViewModel.signOut()
        navController.navigate(Routes.SIGN_IN) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SIGN_IN) {

        composable(Routes.SIGN_IN) {
            SignInScreen(
                viewModel = authViewModel,
                onSignedIn = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) },
                onNavigateToForgot = { navController.navigate(Routes.FORGOT_PASSWORD) },
            )
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(
                viewModel = authViewModel,
                onSignedUp = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                profileViewModel = profileViewModel,
                promotionViewModel = viewModel,
                redemptionViewModel = redemptionViewModel,
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToExplore = { navController.navigate(Routes.EXPLORE) },
                onNavigateToMisPromos = { navController.navigate(Routes.MIS_PROMOS) },
                onNavigateToCreate = { navController.navigate(Routes.CREATE_PROMOTION) },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onLogout = logout,
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onLogout = logout,
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onPasswordChanged = { navController.popBackStack() },
            )
        }

        composable(Routes.EXPLORE) {
            ExploreScreen(
                viewModel = viewModel,
                onPromotionClick = { id -> navController.navigate(Routes.promotionDetail(id)) },
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                },
                onNavigateToCreate = { navController.navigate(Routes.CREATE_PROMOTION) },
                onNavigateToMisPromos = { navController.navigate(Routes.MIS_PROMOS) },
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

        composable(
            route = Routes.PROMOTION_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PROMOTION_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val promotionId = backStackEntry.arguments?.getString(Routes.ARG_PROMOTION_ID).orEmpty()
            val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()

            // US-04: al generarse el código, navega a la pantalla de QR.
            LaunchedEffect(redemptionState.generated) {
                redemptionState.generated?.let { code ->
                    navController.navigate(Routes.qrCode(code.id))
                    redemptionViewModel.consumeGenerated()
                }
            }

            PromotionDetailScreen(
                promotionId = promotionId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onApplyDiscount = { promo -> redemptionViewModel.generate(promo) },
            )
        }

        composable(
            route = Routes.QR_CODE,
            arguments = listOf(navArgument(Routes.ARG_REDEMPTION_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val redemptionId = backStackEntry.arguments?.getString(Routes.ARG_REDEMPTION_ID).orEmpty()
            val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()

            QrCodeScreen(
                code = redemptionState.codeById(redemptionId),
                onBack = { navController.popBackStack() },
                onGoToMisPromos = {
                    navController.navigate(Routes.MIS_PROMOS) {
                        popUpTo(Routes.EXPLORE)
                    }
                },
            )
        }

        composable(Routes.MIS_PROMOS) {
            MisPromosScreen(
                viewModel = redemptionViewModel,
                onCodeClick = { id -> navController.navigate(Routes.qrCode(id)) },
                onNavigateCommunity = { navController.navigate(Routes.CREATE_PROMOTION) },
                onNavigateHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
            )
        }
    }
}
