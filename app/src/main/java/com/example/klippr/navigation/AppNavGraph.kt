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
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.home.presentation.view.HomeScreen
import com.example.klippr.iam.presentation.view.ForgotPasswordScreen
import com.example.klippr.iam.presentation.view.ResetPasswordScreen
import com.example.klippr.iam.presentation.view.SignInScreen
import com.example.klippr.iam.presentation.view.SignUpScreen
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.presentation.view.NotificationScreen
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.profile.presentation.views.ProfileScreen
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.presentation.view.ExploreScreen
import com.example.klippr.promotions.presentation.view.PromotionDetailScreen
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.view.MisPromosScreen
import com.example.klippr.redemption.presentation.view.QrCodeScreen
import com.example.klippr.redemption.presentation.view.RedemptionSuccessScreen
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.settings.presentation.view.SettingsScreen

@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    viewModel: PromotionViewModel,
    redemptionViewModel: RedemptionViewModel,
    communityViewModel: CommunityViewModel,          // ← nuevo
    favoriteViewModel: FavoriteViewModel,
    notificationViewModel: NotificationViewModel,
    sessionStore: SessionDataStore,                  // ← nuevo
    navController: NavHostController = rememberNavController(),
) {
    val logout: () -> Unit = {
        authViewModel.signOut()
        navController.navigate(Routes.SIGN_IN) {
            popUpTo(0) { inclusive = true }
        }
    }

    LaunchedEffect(sessionStore, navController) {
        sessionStore.sessionExpiredEvents.collect {
            authViewModel.markSessionExpired()
            navController.navigate(Routes.SIGN_IN) {
                popUpTo(0) { inclusive = true }
            }
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
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            HomeScreen(
                profileViewModel    = profileViewModel,
                promotionViewModel  = viewModel,
                favoriteViewModel   = favoriteViewModel,
                redemptionViewModel = redemptionViewModel,
                notificationViewModel  = notificationViewModel,
                currentUserId       = currentUserId,
                onNavigateToSettings  = { navController.navigate(Routes.SETTINGS) },
                onNavigateToExplore   = { navController.navigate(Routes.EXPLORE) },
                onNavigateToMisPromos = { navController.navigate(Routes.misPromos(Routes.TAB_CODES)) },
                onNavigateToCommunity = { navController.navigate(Routes.COMMUNITY) },
                onNavigateToQr        = { id -> navController.navigate(Routes.redemptionSuccess(id)) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = profileViewModel,
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
                viewModel = authViewModel,
                onBack    = { navController.popBackStack() },
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
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            ExploreScreen(
                viewModel           = viewModel,
                favoriteViewModel   = favoriteViewModel,
                currentUserId       = currentUserId,
                onBack              = { navController.popBackStack() },
                onNavigateToDetail  = { id -> navController.navigate(Routes.promotionDetail(id)) },
                onNavigateToHome    = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                },
                onNavigateToCommunity = { navController.navigate(Routes.COMMUNITY) },
                onNavigateToMisPromos = { navController.navigate(Routes.misPromos(Routes.TAB_FAVORITES)) },
            )
        }

        composable(
            route = Routes.PROMOTION_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PROMOTION_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val promotionId    = backStackEntry.arguments?.getString(Routes.ARG_PROMOTION_ID).orEmpty()
            val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
            val favoriteState by favoriteViewModel.state.collectAsStateWithLifecycle()
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            val favorite = favoriteState.visibleFavorites.firstOrNull { it.promotionId == promotionId }

            LaunchedEffect(currentUserId) {
                favoriteViewModel.loadFavorites(currentUserId)
            }

            LaunchedEffect(redemptionState.generated) {
                redemptionState.generated?.let { code ->
                    navController.navigate(Routes.redemptionSuccess(code.id))
                    notificationViewModel.notify(
                        type = NotificationType.REDEMPTION_GENERATED,
                        title = "¡Código generado!",
                        message = "Tu código para \"${code.promotionTitle ?: "una promo"}\" está listo.",
                        relatedId = code.id,
                    )
                    redemptionViewModel.consumeGenerated()
                }
            }

            PromotionDetailScreen(
                promotionId     = promotionId,
                viewModel       = viewModel,
                favoriteViewModel = favoriteViewModel,
                currentUserId   = currentUserId,
                onBack          = { navController.popBackStack() },
                onApplyDiscount = { promo ->
                    redemptionViewModel.generate(promo)
                },
                onNavigateToReviews = { navController.navigate(Routes.community(promotionId)) },
                isGenerating    = redemptionState.isGenerating,
                errorMessage    = redemptionState.error,
                isFavoriteOverride = favorite != null,
                onFavoriteSaved = { id ->
                    if (favorite == null) {
                        notificationViewModel.notify(
                            type = NotificationType.FAVORITE_ADDED,
                            title = "Guardado en favoritos",
                            message = "Agregaste una promo a tus favoritos.",
                            relatedId = id,
                        )
                    }
                },
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
                    navController.navigate(Routes.misPromos(Routes.TAB_CODES)) {
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

        composable(
            route = Routes.REDEMPTION_SUCCESS,
            arguments = listOf(navArgument(Routes.ARG_REDEMPTION_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val redemptionId    = backStackEntry.arguments?.getString(Routes.ARG_REDEMPTION_ID).orEmpty()
            val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
            val code            = redemptionState.codeById(redemptionId)

            LaunchedEffect(redemptionId) {
                redemptionViewModel.loadCodeById(redemptionId)
            }

            val goExplore: () -> Unit = {
                navController.navigate(Routes.EXPLORE) { popUpTo(Routes.HOME) }
            }
            RedemptionSuccessScreen(
                code         = code,
                isLoading    = redemptionState.isLoadingCode && code == null,
                errorMessage = redemptionState.codeError.takeIf { code == null },
                onContinue   = goExplore,
                onPromos     = goExplore,
                onComunidad  = { navController.navigate(Routes.COMMUNITY) },
                onInicio     = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onFavoritos  = { navController.navigate(Routes.misPromos(Routes.TAB_FAVORITES)) },
            )
        }

        composable(
            route = Routes.MIS_PROMOS_WITH_TAB,
            arguments = listOf(navArgument(Routes.ARG_TAB) {
                type = NavType.StringType
                defaultValue = Routes.TAB_FAVORITES
            }),
        ) { backStackEntry ->
            val initialTab = when (backStackEntry.arguments?.getString(Routes.ARG_TAB)) {
                Routes.TAB_ARCHIVED -> 1
                Routes.TAB_CODES -> 2
                else -> 0
            }
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            MisPromosScreen(
                viewModel           = redemptionViewModel,
                communityViewModel  = communityViewModel,
                favoriteViewModel   = favoriteViewModel,
                promotionViewModel  = viewModel,
                currentUserId       = currentUserId,
                initialOuterTab     = initialTab,
                onCodeClick         = { id -> navController.navigate(Routes.qrCode(id)) },
                onNavigateToDetail  = { id -> navController.navigate(Routes.promotionDetail(id)) },
                onNavigateCommunity = { navController.navigate(Routes.COMMUNITY) },
                onNavigateHome      = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigatePromos = { navController.navigate(Routes.EXPLORE) },
            )
        }

        composable(
            route = "${Routes.COMMUNITY}?promotionId={promotionId}",
            arguments = listOf(navArgument(Routes.ARG_PROMOTION_ID) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }),
        ) { backStackEntry ->
            val promotionIdFilter = backStackEntry.arguments?.getString(Routes.ARG_PROMOTION_ID)
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            CommunityScreen(
                viewModel         = communityViewModel,
                favoriteViewModel = favoriteViewModel,
                currentUserId     = currentUserId,
                promotionId       = promotionIdFilter,
                onNavigateHome    = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onNavigatePromos  = { navController.navigate(Routes.EXPLORE) },
                onNavigateMisPromos = { navController.navigate(Routes.misPromos(Routes.TAB_FAVORITES)) },
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationScreen(
                viewModel = notificationViewModel,
                onBack = { navController.popBackStack() },
                onNotificationClick = { notif ->
                    notif.relatedId?.let { id ->
                        val route = when (notif.type) {
                            NotificationType.FAVORITE_ADDED -> Routes.promotionDetail(id)
                            NotificationType.REDEMPTION_GENERATED,
                            NotificationType.REDEMPTION_EXPIRING -> Routes.qrCode(id)
                        }
                        navController.navigate(route)
                    }
                },
            )
        }

    }
}
