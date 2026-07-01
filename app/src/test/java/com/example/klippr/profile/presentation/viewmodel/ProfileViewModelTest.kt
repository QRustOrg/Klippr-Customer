package com.example.klippr.profile.presentation.viewmodel

import com.example.klippr.iam.data.store.AuthStore
import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import com.example.klippr.profile.data.store.ProfileStore
import com.example.klippr.profile.domain.model.UserPreference
import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.data.store.RedemptionStore
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
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
    fun load_buildsAccountCenterState() = runTest(dispatcher) {
        val viewModel = ProfileViewModel(
            profileStore = FakeProfileStore(existingPreference = preference("consumer-1")),
            redemptionStore = FakeRedemptionStore(),
            authStore = FakeAuthStore(),
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Samuel Bonifacio", state.profile?.fullName)
        assertEquals("es", state.preference?.languageCode)
        assertEquals(3, state.stats.totalRedemptions)
        assertEquals(1, state.stats.activeRedemptions)
        assertEquals(2, state.stats.redeemedRedemptions)
        assertEquals(listOf("active-1", "redeemed-1", "redeemed-2"), state.latestRedemptions.map { it.id })
        assertFalse(state.isLoading)
    }

    @Test
    fun load_createsDefaultPreferenceWhenMissing() = runTest(dispatcher) {
        val store = FakeProfileStore(existingPreference = null)

        ProfileViewModel(
            profileStore = store,
            redemptionStore = FakeRedemptionStore(),
            authStore = FakeAuthStore(),
        )
        advanceUntilIdle()

        assertEquals("consumer-1", store.createdPreference?.userId)
        assertEquals("es", store.createdPreference?.languageCode)
        assertEquals("America/Bogota", store.createdPreference?.timezone)
        assertEquals(false, store.createdPreference?.smsNotifications)
        assertEquals("private", store.createdPreference?.profileVisibility)
    }

    @Test
    fun savePreference_updatesBackendAndState() = runTest(dispatcher) {
        val store = FakeProfileStore(existingPreference = preference("consumer-1"))
        val viewModel = ProfileViewModel(
            profileStore = store,
            redemptionStore = FakeRedemptionStore(),
            authStore = FakeAuthStore(),
        )
        advanceUntilIdle()

        viewModel.savePreference(
            store.preference!!.copy(
                languageCode = "en",
                pushNotifications = false,
                profileVisibility = "public",
            )
        )
        advanceUntilIdle()

        assertEquals("en", viewModel.state.value.preference?.languageCode)
        assertEquals(false, viewModel.state.value.preference?.pushNotifications)
        assertEquals("public", store.savedPreference?.profileVisibility)
    }

    @Test
    fun load_keepsProfileVisibleWhenPreferenceLoadFails() = runTest(dispatcher) {
        val viewModel = ProfileViewModel(
            profileStore = FakeProfileStore(existingPreference = preference("consumer-1"), failPreferences = true),
            redemptionStore = FakeRedemptionStore(),
            authStore = FakeAuthStore(),
        )

        advanceUntilIdle()

        assertNotNull(viewModel.state.value.profile)
        assertNull(viewModel.state.value.error)
        assertEquals("Error 500", viewModel.state.value.preferenceError)
    }

    @Test
    fun load_keepsProfileVisibleWhenRedemptionLoadFails() = runTest(dispatcher) {
        val viewModel = ProfileViewModel(
            profileStore = FakeProfileStore(existingPreference = preference("consumer-1")),
            redemptionStore = FakeRedemptionStore(failHistory = true),
            authStore = FakeAuthStore(),
        )

        advanceUntilIdle()

        assertNotNull(viewModel.state.value.profile)
        assertNull(viewModel.state.value.error)
        assertEquals(0, viewModel.state.value.stats.totalRedemptions)
        assertEquals("Error 500", viewModel.state.value.activityError)
    }

    private class FakeProfileStore(
        existingPreference: UserPreference?,
        private val failPreferences: Boolean = false,
    ) : ProfileStore {
        var preference: UserPreference? = existingPreference
            private set
        var createdPreference: UserPreference? = null
            private set
        var savedPreference: UserPreference? = null
            private set

        override suspend fun getCurrentProfile(): UserProfile = UserProfile(
            userId = "consumer-1",
            email = "samuel@test.com",
            role = "CONSUMER",
            firstName = "Samuel",
            lastName = "Bonifacio",
            isActive = true,
            memberSince = "11/06/2026",
        )

        override suspend fun getCurrentPreference(): UserPreference? {
            if (failPreferences) throw IllegalStateException("Error 500")
            return preference
        }

        override suspend fun createPreference(preference: UserPreference): UserPreference {
            if (failPreferences) throw IllegalStateException("Error 500")
            createdPreference = preference.copy(id = 7)
            this.preference = createdPreference
            return createdPreference!!
        }

        override suspend fun updatePreference(preference: UserPreference): UserPreference {
            savedPreference = preference
            this.preference = preference
            return preference
        }
    }

    private class FakeRedemptionStore(private val failHistory: Boolean = false) : RedemptionStore {
        override suspend fun generate(consumerId: String, promotion: Promotion): RedemptionCode =
            code("active-1", RedemptionStatus.ACTIVE)

        override suspend fun getByConsumer(consumerId: String): List<RedemptionCode> {
            if (failHistory) throw IllegalStateException("Error 500")
            return listOf(
                code("active-1", RedemptionStatus.ACTIVE),
                code("redeemed-1", RedemptionStatus.REDEEMED),
                code("redeemed-2", RedemptionStatus.REDEEMED),
            )
        }

        override suspend fun getById(id: String): RedemptionCode = code(id, RedemptionStatus.ACTIVE)
        override suspend fun confirm(code: RedemptionCode): RedemptionCode = code.copy(status = RedemptionStatus.REDEEMED)
    }

    private class FakeAuthStore : AuthStore {
        override val session: Flow<Session?> = flowOf(Session("token", user))
        override suspend fun currentUser(): User = user
        override suspend fun signIn(email: String, password: String, rememberMe: Boolean): Session = session()
        override suspend fun signUpConsumer(firstName: String, lastName: String, email: String, password: String): Session = session()
        override suspend fun requestPasswordRecovery(email: String) = Unit
        override suspend fun resetPassword(email: String, newPassword: String) = Unit
        override suspend fun signOut() = Unit

        private fun session() = Session("token", user)
    }
}

private val user = User(userId = "consumer-1", email = "samuel@test.com", role = "CONSUMER")

private fun preference(userId: String) = UserPreference.defaults(userId).copy(id = 5)

private fun code(id: String, status: RedemptionStatus) = RedemptionCode(
    id = id,
    promotionId = "promo-1",
    businessId = "business-1",
    code = id,
    token = id,
    status = status,
    discountAppliedAmount = 12.5,
    expiresAt = null,
    redeemedAt = null,
    blockedAt = null,
    businessName = "Klippr Store",
    promotionTitle = "2x1",
    discountValue = 12.5,
    discountType = null,
    imageKey = null,
)
