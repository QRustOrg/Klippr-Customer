package com.example.klippr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.shared.core.ServiceLocator
import com.example.klippr.shared.presentation.navigation.MainNavHost
import com.example.klippr.shared.presentation.theme.KlipprTheme

/**
 * Composition root. Resuelve el [ServiceLocator] del proceso (en [KlipprApplication]) y crea
 * los ViewModels con scope de Activity mediante el `Factory` de cada companion, para conservar
 * estado compartido entre pantallas (p. ej. el codigo generado entre detalle -> exito -> QR).
 */
class MainActivity : ComponentActivity() {

    private val serviceLocator: ServiceLocator
        get() = (application as KlipprApplication).serviceLocator

    private val authViewModel: AuthViewModel by viewModels { AuthViewModel.Factory(serviceLocator) }
    private val profileViewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory(serviceLocator) }
    private val promotionViewModel: PromotionViewModel by viewModels { PromotionViewModel.Factory(serviceLocator) }
    private val redemptionViewModel: RedemptionViewModel by viewModels { RedemptionViewModel.Factory(serviceLocator) }
    private val communityViewModel: CommunityViewModel by viewModels { CommunityViewModel.Factory(serviceLocator) }
    private val favoriteViewModel: FavoriteViewModel by viewModels { FavoriteViewModel.Factory(serviceLocator) }
    private val notificationViewModel: NotificationViewModel by viewModels { NotificationViewModel.Factory(serviceLocator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlipprTheme {
                MainNavHost(
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    promotionViewModel = promotionViewModel,
                    redemptionViewModel = redemptionViewModel,
                    communityViewModel = communityViewModel,
                    favoriteViewModel = favoriteViewModel,
                    notificationViewModel = notificationViewModel,
                    sessionStore = serviceLocator.sessionStore,
                )
            }
        }
    }
}
