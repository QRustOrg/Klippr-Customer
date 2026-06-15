package com.example.klippr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.klippr.core.database.KlipprDatabase
import com.example.klippr.core.datastore.SessionDataStore
import com.example.klippr.core.network.NetworkModule
import com.example.klippr.iam.data.repository.AuthRepositoryImpl
import com.example.klippr.iam.domain.usecase.GetCurrentUserUseCase
import com.example.klippr.iam.domain.usecase.ResetPasswordUseCase
import com.example.klippr.iam.domain.usecase.SignInUseCase
import com.example.klippr.iam.domain.usecase.SignOutUseCase
import com.example.klippr.iam.domain.usecase.SignUpConsumerUseCase
import com.example.klippr.iam.domain.usecase.VerifyEmailUseCase
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.navigation.AppNavGraph
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
import com.example.klippr.redemption.domain.usecase.GenerateRedemptionUseCase
import com.example.klippr.redemption.domain.usecase.GetConsumerRedemptionsUseCase
import com.example.klippr.redemption.presentation.viewmodel.RedemptionViewModel
import com.example.klippr.ui.theme.KlipprTheme

class MainActivity : ComponentActivity() {

    // Room singleton lazy; se crea la primera vez que se accede.
    private val db by lazy {
        Room.databaseBuilder(applicationContext, KlipprDatabase::class.java, "klippr.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // Sesión local (token + userId) e infraestructura de red compartida con interceptor Bearer.
    private val sessionStore by lazy { SessionDataStore(applicationContext) }
    private val network by lazy { NetworkModule(sessionStore) }

    // IAM
    private val authRepository by lazy { AuthRepositoryImpl(network.authApi, sessionStore) }

    // Perfil: usa el userId de la sesión para consultar GET /api/Users/{userId}.
    private val profileRepository by lazy { ProfileRepositoryImpl(network.profileApi, sessionStore) }

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(
                signInUseCase = SignInUseCase(authRepository),
                signUpConsumerUseCase = SignUpConsumerUseCase(authRepository),
                getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
                signOutUseCase = SignOutUseCase(authRepository),
                verifyEmailUseCase = VerifyEmailUseCase(authRepository),
                resetPasswordUseCase = ResetPasswordUseCase(authRepository),
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
                    getAllPromotions     = GetAllPromotionsUseCase(repository),
                    getActivePromotions  = GetActivePromotionsUseCase(repository),
                    getPromotionById     = GetPromotionByIdUseCase(repository),
                    searchPromotions     = SearchPromotionsUseCase(repository),
                    toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
                    repository           = repository,
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
                    generateRedemption = GenerateRedemptionUseCase(repository),
                    getConsumerRedemptions = GetConsumerRedemptionsUseCase(repository),
                    getCurrentUser = GetCurrentUserUseCase(authRepository),
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
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    viewModel = viewModel,
                    redemptionViewModel = redemptionViewModel,
                )
            }
        }
    }
}
