package com.example.klippr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.klippr.community.data.repository.ReviewRepositoryImpl
import com.example.klippr.community.domain.usecase.CanUserReviewUseCase
import com.example.klippr.community.domain.usecase.GetAllReviewsUseCase
import com.example.klippr.community.domain.usecase.GetReviewCommentsUseCase
import com.example.klippr.community.domain.usecase.PostReviewCommentUseCase
import com.example.klippr.community.domain.usecase.PostReviewUseCase
import com.example.klippr.community.domain.usecase.ToggleLikeUseCase
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.core.database.KlipprDatabase
import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.core.network.NetworkModule
import com.example.klippr.favorites.data.repository.FavoriteRepositoryImpl
import com.example.klippr.favorites.domain.usecase.GetUserFavoritesUseCase
import com.example.klippr.favorites.domain.usecase.RemoveFavoriteUseCase
import com.example.klippr.favorites.domain.usecase.SaveFavoriteUseCase
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.iam.data.repository.AuthRepositoryImpl
import com.example.klippr.iam.domain.usecase.GetCurrentUserUseCase
import com.example.klippr.iam.domain.usecase.RequestPasswordRecoveryUseCase
import com.example.klippr.iam.domain.usecase.ResetPasswordUseCase
import com.example.klippr.iam.domain.usecase.SignInUseCase
import com.example.klippr.iam.domain.usecase.SignOutUseCase
import com.example.klippr.iam.domain.usecase.SignUpConsumerUseCase
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.navigation.AppNavGraph
import com.example.klippr.notification.data.repository.NotificationRepositoryImpl
import com.example.klippr.notification.domain.usecase.AddNotificationUseCase
import com.example.klippr.notification.domain.usecase.GetNotificationsUseCase
import com.example.klippr.notification.domain.usecase.GetUnreadNotificationCountUseCase
import com.example.klippr.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.klippr.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.example.klippr.notification.presentation.viewmodel.NotificationViewModel
import com.example.klippr.profile.data.repository.ProfileRepositoryImpl
import com.example.klippr.profile.domain.usecase.GetUserProfileUseCase
import com.example.klippr.profile.presentation.viewmodel.ProfileViewModel
import com.example.klippr.promotions.data.repository.PromotionRepositoryImpl
import com.example.klippr.promotions.domain.usecase.GetActivePromotionsUseCase
import com.example.klippr.promotions.domain.usecase.GetAllPromotionsUseCase
import com.example.klippr.promotions.domain.usecase.GetPromotionByIdUseCase
import com.example.klippr.promotions.domain.usecase.SearchPromotionsUseCase
import com.example.klippr.promotions.domain.usecase.ToggleFavoriteUseCase
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.redemption.data.mapper.RedemptionMapper
import com.example.klippr.redemption.data.repository.RedemptionRepositoryImpl
import com.example.klippr.redemption.domain.usecase.ConfirmRedemptionUseCase
import com.example.klippr.redemption.domain.usecase.GenerateRedemptionUseCase
import com.example.klippr.redemption.domain.usecase.GetConsumerRedemptionsUseCase
import com.example.klippr.redemption.domain.usecase.GetRedemptionByIdUseCase
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.ui.theme.KlipprTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(applicationContext, KlipprDatabase::class.java, "klippr.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    private val sessionStore by lazy { SessionDataStore(applicationContext) }
    private val network by lazy { NetworkModule(sessionStore) }

    private val authRepository by lazy { AuthRepositoryImpl(network.authApi, sessionStore) }
    private val profileRepository by lazy { ProfileRepositoryImpl(network.profileApi, sessionStore) }

    // ── Community ────────────────────────────────────────────────────────────
    private val reviewRepository by lazy {
        ReviewRepositoryImpl(network.reviewApi, db.reviewDao())
    }

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(
                signInUseCase         = SignInUseCase(authRepository),
                signUpConsumerUseCase = SignUpConsumerUseCase(authRepository),
                getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
                signOutUseCase        = SignOutUseCase(authRepository),
                requestPasswordRecoveryUseCase = RequestPasswordRecoveryUseCase(authRepository),
                resetPasswordUseCase  = ResetPasswordUseCase(authRepository),
            ) as T
        }
    }

    private val profileViewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(
                getUserProfile = GetUserProfileUseCase(profileRepository),
            ) as T
        }
    }

    private val viewModel: PromotionViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = PromotionRepositoryImpl(db.promotionDao(), network.promotionApi, network.profileApi)
                return PromotionViewModel(
                    getAllPromotions       = GetAllPromotionsUseCase(repository),
                    getActivePromotions   = GetActivePromotionsUseCase(repository),
                    getPromotionById      = GetPromotionByIdUseCase(repository),
                    searchPromotions      = SearchPromotionsUseCase(repository),
                    toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
                    repository            = repository,
                ) as T
            }
        }
    }

    private val redemptionViewModel: RedemptionViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val mapper = RedemptionMapper(network.promotionApi)
                val repository = RedemptionRepositoryImpl(network.redemptionApi, mapper)
                return RedemptionViewModel(
                    generateRedemption     = GenerateRedemptionUseCase(repository),
                    getConsumerRedemptions = GetConsumerRedemptionsUseCase(repository),
                    getRedemptionById      = GetRedemptionByIdUseCase(repository),
                    confirmRedemption      = ConfirmRedemptionUseCase(repository),
                    getCurrentUser         = GetCurrentUserUseCase(authRepository),
                ) as T
            }
        }
    }

    private val communityViewModel: CommunityViewModel by viewModels {
        CommunityViewModel.Factory(
            getAllReviewsUseCase  = GetAllReviewsUseCase(reviewRepository),
            postReviewUseCase    = PostReviewUseCase(reviewRepository),
            canUserReviewUseCase = CanUserReviewUseCase(reviewRepository),
            toggleLikeUseCase    = ToggleLikeUseCase(reviewRepository),
            getReviewCommentsUseCase = GetReviewCommentsUseCase(reviewRepository),
            postReviewCommentUseCase = PostReviewCommentUseCase(reviewRepository),
        )
    }

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = FavoriteRepositoryImpl(network.favoriteApi)
                return FavoriteViewModel(
                    getUserFavorites = GetUserFavoritesUseCase(repository),
                    saveFavorite     = SaveFavoriteUseCase(repository),
                    removeFavorite   = RemoveFavoriteUseCase(repository),
                ) as T
            }
        }
    }

    private val notificationViewModel: NotificationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = NotificationRepositoryImpl(db.notificationDao())
                return NotificationViewModel(
                    getNotifications = GetNotificationsUseCase(repository),
                    getUnreadCount   = GetUnreadNotificationCountUseCase(repository),
                    addNotification  = AddNotificationUseCase(repository),
                    markAsRead       = MarkNotificationAsReadUseCase(repository),
                    markAllAsRead    = MarkAllNotificationsAsReadUseCase(repository),
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlipprTheme {
                AppNavGraph(
                    authViewModel       = authViewModel,
                    profileViewModel    = profileViewModel,
                    viewModel           = viewModel,
                    redemptionViewModel = redemptionViewModel,
                    communityViewModel  = communityViewModel,
                    favoriteViewModel   = favoriteViewModel,
                    notificationViewModel = notificationViewModel,
                    sessionStore        = sessionStore,
                )
            }
        }
    }
}
