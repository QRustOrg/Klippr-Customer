package com.example.klippr.notification.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.klippr.notification.domain.model.NotificationType
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.notification.presentation.views.NotificationScreen
import com.example.klippr.promotions.presentation.navigation.PromotionRoutes
import com.example.klippr.redemption.presentation.navigation.RedemptionRoutes

/** Grafo de navegacion del bounded context Notification. */
fun NavGraphBuilder.notificationGraph(
    navController: NavHostController,
    notificationViewModel: NotificationViewModel,
) {
    composable(NotificationRoutes.NOTIFICATIONS) {
        NotificationScreen(
            viewModel = notificationViewModel,
            onBack = { navController.popBackStack() },
            onNotificationClick = { notif ->
                notif.relatedId?.let { id ->
                    val route = when (notif.type) {
                        NotificationType.FAVORITE_ADDED -> PromotionRoutes.promotionDetail(id)
                        NotificationType.REDEMPTION_GENERATED,
                        NotificationType.REDEMPTION_EXPIRING -> RedemptionRoutes.qrCode(id)
                    }
                    navController.navigate(route)
                }
            },
        )
    }
}
