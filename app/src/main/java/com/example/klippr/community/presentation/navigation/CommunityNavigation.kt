package com.example.klippr.community.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.community.presentation.views.CommunityScreen
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.promotions.presentation.navigation.PromotionRoutes
import com.example.klippr.redemption.presentation.navigation.RedemptionRoutes
import com.example.klippr.shared.data.store.SessionDataStore
import com.example.klippr.shared.presentation.navigation.MainRoutes

/** Grafo de navegacion del bounded context Community (resenas). */
fun NavGraphBuilder.communityGraph(
    navController: NavHostController,
    communityViewModel: CommunityViewModel,
    favoriteViewModel: FavoriteViewModel,
    sessionStore: SessionDataStore,
) {
    composable(
        route = CommunityRoutes.COMMUNITY_WITH_PROMOTION,
        arguments = listOf(navArgument(CommunityRoutes.ARG_PROMOTION_ID) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        }),
    ) { backStackEntry ->
        val promotionIdFilter = backStackEntry.arguments?.getString(CommunityRoutes.ARG_PROMOTION_ID)
        val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
        val currentUserId = session?.user?.userId ?: ""
        CommunityScreen(
            viewModel = communityViewModel,
            favoriteViewModel = favoriteViewModel,
            currentUserId = currentUserId,
            promotionId = promotionIdFilter,
            onNavigateHome = { navController.navigate(MainRoutes.HOME) { popUpTo(MainRoutes.HOME) { inclusive = true } } },
            onNavigatePromos = { navController.navigate(PromotionRoutes.EXPLORE) },
            onNavigateMisPromos = { navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_FAVORITES)) },
        )
    }
}
