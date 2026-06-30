package com.example.klippr.shared.core

import android.content.Context
import androidx.room.Room
import com.example.klippr.community.data.network.ReviewWebService
import com.example.klippr.community.data.store.ReviewStore
import com.example.klippr.community.data.store.ReviewStoreImpl
import com.example.klippr.favorites.data.network.FavoriteWebService
import com.example.klippr.favorites.data.store.FavoriteStore
import com.example.klippr.favorites.data.store.FavoriteStoreImpl
import com.example.klippr.iam.data.network.AuthWebService
import com.example.klippr.iam.data.store.AuthStore
import com.example.klippr.iam.data.store.AuthStoreImpl
import com.example.klippr.notification.data.store.NotificationStore
import com.example.klippr.notification.data.store.NotificationStoreImpl
import com.example.klippr.profile.data.network.ProfileWebService
import com.example.klippr.profile.data.store.ProfileStore
import com.example.klippr.profile.data.store.ProfileStoreImpl
import com.example.klippr.promotions.data.network.PromotionWebService
import com.example.klippr.promotions.data.store.PromotionStore
import com.example.klippr.promotions.data.store.PromotionStoreImpl
import com.example.klippr.redemption.data.network.RedemptionWebService
import com.example.klippr.redemption.data.store.RedemptionStore
import com.example.klippr.redemption.data.store.RedemptionStoreImpl
import com.example.klippr.shared.data.local.KlipprDatabase
import com.example.klippr.shared.data.network.ApiClient
import com.example.klippr.shared.data.store.SessionDataStore

// @author Samuel Bonifacio
/**
 * Contenedor manual de dependencias del proceso (DI sin framework).
 *
 * Posee la infraestructura compartida (DataStore de sesion, base Room, Retrofit via [ApiClient])
 * y construye los Stores por bounded context. Los ViewModels resuelven sus dependencias a traves
 * del locator mediante el `Factory` expuesto en su companion.
 */
class ServiceLocator(context: Context) {

    private val appContext: Context = context.applicationContext

    // ── Infraestructura compartida ─────────────────────────────────────────────
    val sessionStore: SessionDataStore by lazy { SessionDataStore(appContext) }

    private val db: KlipprDatabase by lazy {
        Room.databaseBuilder(appContext, KlipprDatabase::class.java, "klippr.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    init {
        // Cablea la fuente de token/401 antes de crear cualquier WebService.
        ApiClient.install(sessionStore)
    }

    // ── WebServices (Retrofit) ─────────────────────────────────────────────────
    private val authWebService: AuthWebService by lazy { ApiClient.create() }
    private val profileWebService: ProfileWebService by lazy { ApiClient.create() }
    private val promotionWebService: PromotionWebService by lazy { ApiClient.create() }
    private val redemptionWebService: RedemptionWebService by lazy { ApiClient.create() }
    private val reviewWebService: ReviewWebService by lazy { ApiClient.create() }
    private val favoriteWebService: FavoriteWebService by lazy { ApiClient.create() }

    // ── Stores por bounded context (expuestos como interfaz) ───────────────────
    val authStore: AuthStore by lazy { AuthStoreImpl(authWebService, sessionStore) }
    val profileStore: ProfileStore by lazy { ProfileStoreImpl(profileWebService, sessionStore) }
    val promotionStore: PromotionStore by lazy {
        PromotionStoreImpl(db.promotionDao(), promotionWebService, profileWebService)
    }
    val redemptionStore: RedemptionStore by lazy {
        RedemptionStoreImpl(redemptionWebService, promotionWebService)
    }
    val reviewStore: ReviewStore by lazy { ReviewStoreImpl(reviewWebService, db.reviewDao()) }
    val favoriteStore: FavoriteStore by lazy { FavoriteStoreImpl(favoriteWebService) }
    val notificationStore: NotificationStore by lazy { NotificationStoreImpl(db.notificationDao()) }
}
