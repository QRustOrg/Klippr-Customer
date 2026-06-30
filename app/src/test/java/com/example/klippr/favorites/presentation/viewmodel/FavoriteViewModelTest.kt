package com.example.klippr.favorites.presentation.viewmodel

import com.example.klippr.favorites.domain.model.Favorite
import com.example.klippr.favorites.data.store.FavoriteStore
import com.example.klippr.favorites.application.usecase.ArchiveFavoriteUseCase
import com.example.klippr.favorites.application.usecase.GetFavoriteByIdUseCase
import com.example.klippr.favorites.application.usecase.GetUserFavoritesUseCase
import com.example.klippr.favorites.application.usecase.RemoveFavoriteUseCase
import com.example.klippr.favorites.application.usecase.RestoreFavoriteUseCase
import com.example.klippr.favorites.application.usecase.SaveFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addFavorite_savesReloadsAndRunsCallback() = runTest(dispatcher) {
        val repository = FakeFavoriteRepository()
        val viewModel = viewModel(repository)
        var callbackCalled = false

        viewModel.addFavorite("user-1", "promo-1") {
            callbackCalled = true
        }
        advanceUntilIdle()

        assertEquals(listOf("user-1" to "promo-1"), repository.savedFavorites)
        assertEquals(listOf(false, true), repository.requestedArchiveStates)
        assertTrue(callbackCalled)
    }

    @Test
    fun archiveFavorite_archivesAndReloads() = runTest(dispatcher) {
        val repository = FakeFavoriteRepository()
        val viewModel = viewModel(repository)
        var callbackCalled = false

        viewModel.archiveFavorite("favorite-1", "user-1") {
            callbackCalled = true
        }
        advanceUntilIdle()

        assertEquals(listOf("favorite-1" to "user-1"), repository.archivedFavorites)
        assertEquals(listOf(false, true), repository.requestedArchiveStates)
        assertTrue(callbackCalled)
    }

    @Test
    fun restoreFavorite_restoresAndReloads() = runTest(dispatcher) {
        val repository = FakeFavoriteRepository()
        val viewModel = viewModel(repository)
        var callbackCalled = false

        viewModel.restoreFavorite("favorite-1", "user-1") {
            callbackCalled = true
        }
        advanceUntilIdle()

        assertEquals(listOf("favorite-1" to "user-1"), repository.restoredFavorites)
        assertEquals(listOf(false, true), repository.requestedArchiveStates)
        assertTrue(callbackCalled)
    }

    @Test
    fun deleteFavorite_removesReloadsAndRunsCallback() = runTest(dispatcher) {
        val repository = FakeFavoriteRepository()
        val viewModel = viewModel(repository)
        var callbackCalled = false

        viewModel.deleteFavorite("favorite-1", "user-1") {
            callbackCalled = true
        }
        advanceUntilIdle()

        assertEquals(listOf("favorite-1" to "user-1"), repository.removedFavorites)
        assertEquals(listOf(false, true), repository.requestedArchiveStates)
        assertTrue(callbackCalled)
    }

    @Test
    fun deleteFavorite_refreshesEvenWhenRemoveFails() = runTest(dispatcher) {
        val repository = FakeFavoriteRepository(removeError = IllegalStateException("No encontrado"))
        val viewModel = viewModel(repository)
        var callbackCalled = false

        viewModel.deleteFavorite("favorite-1", "user-1") {
            callbackCalled = true
        }
        advanceUntilIdle()

        assertEquals(listOf("favorite-1" to "user-1"), repository.removedFavorites)
        assertEquals(listOf(false, true), repository.requestedArchiveStates)
        assertEquals("No encontrado", viewModel.state.value.error)
        assertEquals(false, callbackCalled)
    }

    @Test
    fun openFavoriteDetails_loadsFavoriteAndReturnsPromotionId() = runTest(dispatcher) {
        val repository = FakeFavoriteRepository()
        val viewModel = viewModel(repository)
        var resolvedPromotionId: String? = null

        viewModel.openFavoriteDetails("favorite-1") { promotionId ->
            resolvedPromotionId = promotionId
        }
        advanceUntilIdle()

        assertEquals("favorite-1", repository.requestedFavoriteId)
        assertEquals("promo-1", resolvedPromotionId)
    }

    private fun viewModel(repository: FavoriteStore) = FavoriteViewModel(
        getUserFavorites = GetUserFavoritesUseCase(repository),
        getFavoriteById = GetFavoriteByIdUseCase(repository),
        saveFavorite = SaveFavoriteUseCase(repository),
        removeFavorite = RemoveFavoriteUseCase(repository),
        archiveFavoriteUseCase = ArchiveFavoriteUseCase(repository),
        restoreFavoriteUseCase = RestoreFavoriteUseCase(repository),
    )

    private class FakeFavoriteRepository(
        private val removeError: Throwable? = null,
    ) : FavoriteStore {
        val savedFavorites = mutableListOf<Pair<String, String>>()
        val archivedFavorites = mutableListOf<Pair<String, String>>()
        val restoredFavorites = mutableListOf<Pair<String, String>>()
        val removedFavorites = mutableListOf<Pair<String, String>>()
        val requestedArchiveStates = mutableListOf<Boolean>()
        var requestedFavoriteId: String? = null

        override suspend fun getFavoritesByUser(userId: String, archived: Boolean): List<Favorite> {
            requestedArchiveStates += archived
            return listOf(favorite(isArchived = archived))
        }

        override suspend fun getFavoriteById(favoriteId: String): Favorite {
            requestedFavoriteId = favoriteId
            return favorite(favoriteId = favoriteId)
        }

        override suspend fun saveFavorite(userId: String, promotionId: String): Favorite {
            savedFavorites += userId to promotionId
            return favorite(userId = userId, promotionId = promotionId)
        }

        override suspend fun removeFavorite(favoriteId: String, userId: String) {
            removedFavorites += favoriteId to userId
            removeError?.let { throw it }
        }

        override suspend fun archiveFavorite(favoriteId: String, userId: String) {
            archivedFavorites += favoriteId to userId
        }

        override suspend fun restoreFavorite(favoriteId: String, userId: String) {
            restoredFavorites += favoriteId to userId
        }

        private fun favorite(
            favoriteId: String = "favorite-1",
            userId: String = "user-1",
            promotionId: String = "promo-1",
            isArchived: Boolean = false,
        ) = Favorite(
            favoriteId = favoriteId,
            userId = userId,
            promotionId = promotionId,
            isArchived = isArchived,
        )
    }
}
