package com.example.klippr.promotions.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.klippr.community.presentation.navigation.CommunityRoutes
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.promotions.presentation.views.ExploreScreen
import com.example.klippr.promotions.presentation.views.PromotionDetailScreen
import com.example.klippr.redemption.presentation.navigation.RedemptionRoutes
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.shared.data.store.SessionDataStore
import com.example.klippr.shared.presentation.navigation.MainRoutes

/** Grafo de navegacion del bounded context Promotions (explorar + detalle). */
fun NavGraphBuilder.promotionGraph(
    navController: NavHostController,
    promotionViewModel: PromotionViewModel,
    favoriteViewModel: FavoriteViewModel,
    redemptionViewModel: RedemptionViewModel,
    notificationViewModel: NotificationViewModel,
    sessionStore: SessionDataStore,
) {
    composable(PromotionRoutes.EXPLORE) {
        val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
        val currentUserId = session?.user?.userId ?: ""
        ExploreScreen(
            viewModel = promotionViewModel,
            favoriteViewModel = favoriteViewModel,
            currentUserId = currentUserId,
            onBack = { navController.popBackStack() },
            onNavigateToDetail = { id -> navController.navigate(PromotionRoutes.promotionDetail(id)) },
            onNavigateToHome = {
                navController.navigate(MainRoutes.HOME) { popUpTo(MainRoutes.HOME) { inclusive = true } }
            },
            onNavigateToCommunity = { navController.navigate(CommunityRoutes.COMMUNITY) },
            onNavigateToMisPromos = { navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_FAVORITES)) },
        )
    }

    composable(
        route = PromotionRoutes.PROMOTION_DETAIL,
        arguments = listOf(navArgument(PromotionRoutes.ARG_PROMOTION_ID) { type = NavType.StringType }),
    ) { backStackEntry ->
        val promotionId = backStackEntry.arguments?.getString(PromotionRoutes.ARG_PROMOTION_ID).orEmpty()
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
                navController.navigate(RedemptionRoutes.redemptionSuccess(code.id))
                notificationViewModel.notify(
                    type = NotificationType.REDEMPTION_GENERATED,
                    title = "\u00a1C\u00f3digo generado!",
                    message = "Tu c\u00f3digo para \"${code.promotionTitle ?: "una promo"}\" est\u00e1 listo.",
                    relatedId = code.id,
                )
                redemptionViewModel.consumeGenerated()
            }
        }

        PromotionDetailScreen(
            promotionId = promotionId,
            viewModel = promotionViewModel,
            favoriteViewModel = favoriteViewModel,
            currentUserId = currentUserId,
            onBack = { navController.popBackStack() },
            onApplyDiscount = { promo ->
                redemptionViewModel.generate(promo)
            },
            onNavigateToReviews = { navController.navigate(CommunityRoutes.community(promotionId)) },
            isGenerating = redemptionState.isGenerating,
            errorMessage = redemptionState.error,
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
}
