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
import com.example.klippr.community.presentation.view.CommunityScreen
import com.example.klippr.community.presentation.view.ReviewBottomSheet
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.core.presentation.SplashScreen
import com.example.klippr.home.presentation.view.HomeScreen
import com.example.klippr.iam.presentation.view.ForgotPasswordScreen
import com.example.klippr.iam.presentation.view.ResetPasswordScreen
import com.example.klippr.iam.presentation.view.SignInScreen
import com.example.klippr.iam.presentation.view.SignUpScreen
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.profile.presentation.view.ProfileScreen
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.presentation.view.ExploreScreen
import com.example.klippr.promotions.presentation.view.PromotionDetailScreen
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.view.MisPromosScreen
import com.example.klippr.redemption.presentation.view.QrCodeScreen
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.settings.presentation.view.SettingsScreen

@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    viewModel: PromotionViewModel,
    redemptionViewModel: RedemptionViewModel,
    communityViewModel: CommunityViewModel,          // ← nuevo
    sessionStore: SessionDataStore,                  // ← nuevo
    navController: NavHostController = rememberNavController(),
) {
    val logout: () -> Unit = {
        authViewModel.signOut()
        navController.navigate(Routes.SIGN_IN) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

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
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                profileViewModel    = profileViewModel,
                promotionViewModel  = viewModel,
                redemptionViewModel = redemptionViewModel,
                onNavigateToSettings  = { navController.navigate(Routes.SETTINGS) },
                onNavigateToExplore   = { navController.navigate(Routes.EXPLORE) },
                onNavigateToMisPromos = { navController.navigate(Routes.MIS_PROMOS) },
                onNavigateToCommunity = { navController.navigate(Routes.COMMUNITY) },
                onNavigateToQr        = { id -> navController.navigate(Routes.qrCode(id)) },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack              = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onLogout            = logout,
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onBack    = { navController.popBackStack() },
                onLogout  = logout,
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel       = authViewModel,
                onEmailVerified = { navController.navigate(Routes.RESET_PASSWORD) },
                onBack          = { navController.popBackStack() },
            )
        }

        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(
                viewModel         = authViewModel,
                onPasswordChanged = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.EXPLORE) {
            ExploreScreen(
                viewModel           = viewModel,
                redemptionViewModel = redemptionViewModel,
                onBack              = { navController.popBackStack() },
                onNavigateToQr      = { id -> navController.navigate(Routes.qrCode(id)) },
                onNavigateToHome    = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                },
                onNavigateToCommunity = { navController.navigate(Routes.COMMUNITY) },
                onNavigateToMisPromos = { navController.navigate(Routes.MIS_PROMOS) },
            )
        }

        composable(
            route = Routes.PROMOTION_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PROMOTION_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val promotionId    = backStackEntry.arguments?.getString(Routes.ARG_PROMOTION_ID).orEmpty()
            val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(redemptionState.generated) {
                redemptionState.generated?.let { code ->
                    navController.navigate(Routes.qrCode(code.id))
                    redemptionViewModel.consumeGenerated()
                }
            }

            PromotionDetailScreen(
                promotionId     = promotionId,
                viewModel       = viewModel,
                onBack          = { navController.popBackStack() },
                onApplyDiscount = { promo -> redemptionViewModel.generate(promo) },
            )
        }

        composable(
            route = Routes.QR_CODE,
            arguments = listOf(navArgument(Routes.ARG_REDEMPTION_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val redemptionId    = backStackEntry.arguments?.getString(Routes.ARG_REDEMPTION_ID).orEmpty()
            val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
            val communityState  by communityViewModel.uiState.collectAsStateWithLifecycle()
            val code            = redemptionState.codeById(redemptionId)

            LaunchedEffect(redemptionId) {
                redemptionViewModel.loadCodeById(redemptionId)
            }

            if (communityState.isReviewSheetOpen) {
                ReviewBottomSheet(
                    uiState          = communityState,
                    onDismiss        = { communityViewModel.closeReviewSheet() },
                    onRatingChanged  = communityViewModel::onRatingChanged,
                    onCommentChanged = communityViewModel::onCommentChanged,
                    onSubmit         = communityViewModel::submitReview,
                )
            }

            QrCodeScreen(
                code          = code,
                isLoading     = redemptionState.isLoadingCode && code == null,
                errorMessage  = redemptionState.codeError.takeIf { code == null },
                onBack        = { navController.popBackStack() },
                onGoToMisPromos = {
                    navController.navigate(Routes.MIS_PROMOS) {
                        popUpTo(Routes.EXPLORE)
                    }
                },
                onRetry       = { redemptionViewModel.loadCodeById(redemptionId) },
                onLeaveReview = code?.let { c ->
                    {
                        communityViewModel.openReviewSheetForRedeemed(
                            c.promotionId,
                            c.promotionTitle ?: "Promoción",
                        )
                    }
                },
            )
        }

        composable(Routes.MIS_PROMOS) {
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            MisPromosScreen(
                viewModel           = redemptionViewModel,
                communityViewModel  = communityViewModel,
                currentUserId       = currentUserId,
                onCodeClick         = { id -> navController.navigate(Routes.qrCode(id)) },
                onNavigateCommunity = { navController.navigate(Routes.COMMUNITY) },
                onNavigateHome      = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigatePromos = { navController.navigate(Routes.EXPLORE) },
            )
        }

        composable(Routes.COMMUNITY) {
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            CommunityScreen(
                viewModel         = communityViewModel,
                currentUserId     = currentUserId,
                onNavigateHome    = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onNavigatePromos  = { navController.navigate(Routes.EXPLORE) },
                onNavigateMisPromos = { navController.navigate(Routes.MIS_PROMOS) },
            )
        }
    }
}