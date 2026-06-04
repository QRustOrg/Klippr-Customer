package com.example.la25_11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.klippr.core.database.KlipprDatabase
import com.example.klippr.navigation.AppNavGraph
import com.example.klippr.promotions.data.remote.api.PromotionApiService
import com.example.klippr.promotions.data.repository.PromotionRepositoryImpl
import com.example.klippr.promotions.domain.usecase.GetActivePromotionsUseCase
import com.example.klippr.promotions.domain.usecase.GetPromotionByIdUseCase
import com.example.klippr.promotions.domain.usecase.SearchPromotionsUseCase
import com.example.klippr.promotions.domain.usecase.ToggleFavoriteUseCase
import com.example.klippr.promotions.presentation.viewmodel.PromotionViewModel
import com.example.klippr.ui.theme.KlipprTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    // Room singleton lazy; se crea la primera vez que se accede.
    private val db by lazy {
        Room.databaseBuilder(applicationContext, KlipprDatabase::class.java, "klippr.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // Retrofit, fetch de API
    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://klippr-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PromotionApiService::class.java)
    }

    private val viewModel: PromotionViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = PromotionRepositoryImpl(db.promotionDao(), api)
                return PromotionViewModel(
                    getActivePromotions  = GetActivePromotionsUseCase(repository),
                    getPromotionById     = GetPromotionByIdUseCase(repository),
                    searchPromotions     = SearchPromotionsUseCase(repository),
                    toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
                    repository           = repository,
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlipprTheme {
                AppNavGraph(viewModel = viewModel)
            }
        }
    }
}
