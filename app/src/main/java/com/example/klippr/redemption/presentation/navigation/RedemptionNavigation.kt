package com.example.klippr.redemption.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.klippr.community.presentation.navigation.CommunityRoutes
import com.example.klippr.community.presentation.views.ReviewBottomSheet
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.promotions.presentation.navigation.PromotionRoutes
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.redemption.presentation.views.MisPromosScreen
import com.example.klippr.redemption.presentation.views.QrCodeScreen
import com.example.klippr.redemption.presentation.views.RedemptionSuccessScreen
import com.example.klippr.shared.data.store.SessionDataStore
import com.example.klippr.shared.presentation.navigation.MainRoutes

/** Grafo de navegacion del bounded context Redemption (QR, exito y "Mis Promos"). */
fun NavGraphBuilder.redemptionGraph(
    navController: NavHostController,
    redemptionViewModel: RedemptionViewModel,
    communityViewModel: CommunityViewModel,
    favoriteViewModel: FavoriteViewModel,
    promotionViewModel: PromotionViewModel,
    sessionStore: SessionDataStore,
) {
    composable(
        route = RedemptionRoutes.QR_CODE,
        arguments = listOf(navArgument(RedemptionRoutes.ARG_REDEMPTION_ID) { type = NavType.StringType }),
    ) { backStackEntry ->
        val redemptionId = backStackEntry.arguments?.getString(RedemptionRoutes.ARG_REDEMPTION_ID).orEmpty()
        val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
        val communityState by communityViewModel.uiState.collectAsStateWithLifecycle()
        val code = redemptionState.codeById(redemptionId)

        LaunchedEffect(redemptionId) {
            redemptionViewModel.loadCodeById(redemptionId)
        }

        if (communityState.isReviewSheetOpen) {
            ReviewBottomSheet(
                uiState = communityState,
                onDismiss = { communityViewModel.closeReviewSheet() },
                onRatingChanged = communityViewModel::onRatingChanged,
                onCommentChanged = communityViewModel::onCommentChanged,
                onSubmit = communityViewModel::submitReview,
            )
        }

        QrCodeScreen(
            code = code,
            isLoading = redemptionState.isLoadingCode && code == null,
            errorMessage = redemptionState.codeError.takeIf { code == null },
            onBack = { navController.popBackStack() },
            onGoToMisPromos = {
                navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_CODES)) {
                    popUpTo(PromotionRoutes.EXPLORE)
                }
            },
            onRetry = { redemptionViewModel.loadCodeById(redemptionId) },
            onLeaveReview = code?.let { c ->
                {
                    communityViewModel.openReviewSheetForRedeemed(
                        c.promotionId,
                        c.promotionTitle ?: "Promoci\u00f3n",
                    )
                }
            },
        )
    }

    composable(
        route = RedemptionRoutes.REDEMPTION_SUCCESS,
        arguments = listOf(navArgument(RedemptionRoutes.ARG_REDEMPTION_ID) { type = NavType.StringType }),
    ) { backStackEntry ->
        val redemptionId = backStackEntry.arguments?.getString(RedemptionRoutes.ARG_REDEMPTION_ID).orEmpty()
        val redemptionState by redemptionViewModel.state.collectAsStateWithLifecycle()
        val code = redemptionState.codeById(redemptionId)

        LaunchedEffect(redemptionId) {
            redemptionViewModel.loadCodeById(redemptionId)
        }

        val goExplore: () -> Unit = {
            navController.navigate(PromotionRoutes.EXPLORE) { popUpTo(MainRoutes.HOME) }
        }
        RedemptionSuccessScreen(
            code = code,
            isLoading = redemptionState.isLoadingCode && code == null,
            errorMessage = redemptionState.codeError.takeIf { code == null },
            onContinue = goExplore,
            onPromos = goExplore,
            onComunidad = { navController.navigate(CommunityRoutes.COMMUNITY) },
            onInicio = { navController.navigate(MainRoutes.HOME) { popUpTo(MainRoutes.HOME) { inclusive = true } } },
            onFavoritos = { navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_FAVORITES)) },
        )
    }

    composable(
        route = RedemptionRoutes.MIS_PROMOS_WITH_TAB,
        arguments = listOf(navArgument(RedemptionRoutes.ARG_TAB) {
            type = NavType.StringType
            defaultValue = RedemptionRoutes.TAB_FAVORITES
        }),
    ) { backStackEntry ->
        val initialTab = when (backStackEntry.arguments?.getString(RedemptionRoutes.ARG_TAB)) {
            RedemptionRoutes.TAB_ARCHIVED -> 1
            RedemptionRoutes.TAB_CODES -> 2
            else -> 0
        }
        val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
        val currentUserId = session?.user?.userId ?: ""
        MisPromosScreen(
            viewModel = redemptionViewModel,
            communityViewModel = communityViewModel,
            favoriteViewModel = favoriteViewModel,
            promotionViewModel = promotionViewModel,
            currentUserId = currentUserId,
            initialOuterTab = initialTab,
            onCodeClick = { id -> navController.navigate(RedemptionRoutes.qrCode(id)) },
            onNavigateToDetail = { id -> navController.navigate(PromotionRoutes.promotionDetail(id)) },
            onNavigateCommunity = { navController.navigate(CommunityRoutes.COMMUNITY) },
            onNavigateHome = {
                navController.navigate(MainRoutes.HOME) {
                    popUpTo(MainRoutes.HOME) { inclusive = true }
                }
            },
            onNavigatePromos = { navController.navigate(PromotionRoutes.EXPLORE) },
        )
    }
}
