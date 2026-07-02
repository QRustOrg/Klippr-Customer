package com.example.klippr.shared.presentation.navigation

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
import com.example.klippr.community.presentation.navigation.CommunityRoutes
import com.example.klippr.community.presentation.navigation.communityGraph
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.iam.presentation.navigation.AuthRoutes
import com.example.klippr.iam.presentation.navigation.authGraph
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.notification.presentation.navigation.NotificationRoutes
import com.example.klippr.notification.presentation.navigation.notificationGraph
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.preferences.presentation.viewmodel.PreferenceViewModel
import com.example.klippr.profile.presentation.navigation.ProfileRoutes
import com.example.klippr.profile.presentation.navigation.profileGraph
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.presentation.navigation.PromotionRoutes
import com.example.klippr.promotions.presentation.navigation.promotionGraph
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.navigation.RedemptionRoutes
import com.example.klippr.redemption.presentation.navigation.redemptionGraph
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.shared.data.store.SessionDataStore
import com.example.klippr.shared.presentation.views.SplashScreen
import com.example.klippr.shared.presentation.views.home.HomeScreen
import com.example.klippr.settings.presentation.view.SettingsDetailScreen
import com.example.klippr.settings.presentation.view.SettingsScreen

/**
 * Host de navegacion principal. Compone el grafo de cada bounded context y las pantallas
 * de shell (splash, home, settings). Reemplaza al antiguo AppNavGraph monolitico.
 *
 * Los ViewModels se crean en [com.example.klippr.MainActivity] (con scope de Activity para
 * conservar estado compartido entre pantallas) y se distribuyen a cada grafo.
 */
@Composable
fun MainNavHost(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    preferenceViewModel: PreferenceViewModel,
    promotionViewModel: PromotionViewModel,
    redemptionViewModel: RedemptionViewModel,
    communityViewModel: CommunityViewModel,
    favoriteViewModel: FavoriteViewModel,
    notificationViewModel: NotificationViewModel,
    sessionStore: SessionDataStore,
    navController: NavHostController = rememberNavController(),
) {
    val logout: () -> Unit = {
        authViewModel.signOut()
        navController.navigate(AuthRoutes.SIGN_IN) {
            popUpTo(0) { inclusive = true }
        }
    }

    LaunchedEffect(sessionStore, navController) {
        sessionStore.sessionExpiredEvents.collect {
            authViewModel.markSessionExpired()
            navController.navigate(AuthRoutes.SIGN_IN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = MainRoutes.SPLASH) {

        composable(MainRoutes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(AuthRoutes.SIGN_IN) {
                        popUpTo(MainRoutes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(MainRoutes.HOME) {
            val session by sessionStore.session.collectAsStateWithLifecycle(initialValue = null)
            val currentUserId = session?.user?.userId ?: ""
            HomeScreen(
                profileViewModel = profileViewModel,
                promotionViewModel = promotionViewModel,
                favoriteViewModel = favoriteViewModel,
                redemptionViewModel = redemptionViewModel,
                notificationViewModel = notificationViewModel,
                currentUserId = currentUserId,
                onNavigateToSettings = { navController.navigate(MainRoutes.SETTINGS) },
                onNavigateToExplore = { navController.navigate(PromotionRoutes.EXPLORE) },
                onNavigateToMisPromos = { navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_CODES)) },
                onNavigateToCommunity = { navController.navigate(CommunityRoutes.COMMUNITY) },
                onNavigateToQr = { id -> navController.navigate(RedemptionRoutes.redemptionSuccess(id)) },
                onNavigateToNotifications = { navController.navigate(NotificationRoutes.NOTIFICATIONS) },
            )
        }

        composable(MainRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate(ProfileRoutes.PROFILE) },
                onNavigateToDetail = { section -> navController.navigate(MainRoutes.settingsDetail(section)) },
                onLogout = logout,
                onNavigateHome = {
                    navController.navigate(MainRoutes.HOME) {
                        popUpTo(MainRoutes.HOME) { inclusive = true }
                    }
                },
                onNavigateFavorites = { navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_FAVORITES)) },
                onNavigatePromos = { navController.navigate(PromotionRoutes.EXPLORE) },
                onNavigateCommunity = { navController.navigate(CommunityRoutes.COMMUNITY) },
            )
        }

        composable(
            route = MainRoutes.SETTINGS_DETAIL,
            arguments = listOf(navArgument(MainRoutes.ARG_SETTINGS_SECTION) { type = NavType.StringType }),
        ) { backStackEntry ->
            SettingsDetailScreen(
                sectionKey = backStackEntry.arguments?.getString(MainRoutes.ARG_SETTINGS_SECTION).orEmpty(),
                viewModel = preferenceViewModel,
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(MainRoutes.HOME) {
                        popUpTo(MainRoutes.HOME) { inclusive = true }
                    }
                },
                onNavigateFavorites = { navController.navigate(RedemptionRoutes.misPromos(RedemptionRoutes.TAB_FAVORITES)) },
                onNavigatePromos = { navController.navigate(PromotionRoutes.EXPLORE) },
                onNavigateCommunity = { navController.navigate(CommunityRoutes.COMMUNITY) },
            )
        }

        authGraph(navController = navController, authViewModel = authViewModel)

        profileGraph(
            navController = navController,
            profileViewModel = profileViewModel,
            onLogout = logout,
        )

        promotionGraph(
            navController = navController,
            promotionViewModel = promotionViewModel,
            favoriteViewModel = favoriteViewModel,
            redemptionViewModel = redemptionViewModel,
            notificationViewModel = notificationViewModel,
            sessionStore = sessionStore,
        )

        redemptionGraph(
            navController = navController,
            redemptionViewModel = redemptionViewModel,
            communityViewModel = communityViewModel,
            favoriteViewModel = favoriteViewModel,
            promotionViewModel = promotionViewModel,
            sessionStore = sessionStore,
        )

        communityGraph(
            navController = navController,
            communityViewModel = communityViewModel,
            favoriteViewModel = favoriteViewModel,
            sessionStore = sessionStore,
        )

        notificationGraph(
            navController = navController,
            notificationViewModel = notificationViewModel,
        )
    }
}
