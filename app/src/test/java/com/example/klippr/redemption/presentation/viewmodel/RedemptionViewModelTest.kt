package com.example.klippr.redemption.presentation.viewmodel

import com.example.klippr.iam.application.usecase.GetCurrentUserUseCase
import com.example.klippr.iam.data.store.AuthStore
import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import com.example.klippr.redemption.application.usecase.ConfirmRedemptionUseCase
import com.example.klippr.redemption.application.usecase.GenerateRedemptionUseCase
import com.example.klippr.redemption.application.usecase.GetConsumerRedemptionsUseCase
import com.example.klippr.redemption.application.usecase.GetRedemptionByIdUseCase
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
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class RedemptionViewModelTest {
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
    fun clearFinished_hidesRedeemedAndExpiredOnly() = runTest(dispatcher) {
        val store = FakeRedemptionStore()
        val viewModel = viewModel(store)

        viewModel.loadHistory()
        advanceUntilIdle()
        viewModel.clearFinished()
        advanceUntilIdle()

        assertEquals(listOf("active-1"), viewModel.state.value.codes.map { it.id })
    }

    @Test
    fun generate_withExpiredPromotionDoesNotCallStoreAndShowsEligibilityError() = runTest(dispatcher) {
        val store = FakeRedemptionStore()
        val viewModel = viewModel(store)

        viewModel.generate(promotion(endDate = Instant.EPOCH))
        advanceUntilIdle()

        assertFalse(store.generateCalled)
        assertEquals("Esta promoción ya venció.", viewModel.state.value.error)
    }

    private fun viewModel(store: RedemptionStore) = RedemptionViewModel(
        generateRedemption = GenerateRedemptionUseCase(store),
        getConsumerRedemptions = GetConsumerRedemptionsUseCase(store),
        getRedemptionById = GetRedemptionByIdUseCase(store),
        confirmRedemption = ConfirmRedemptionUseCase(store),
        getCurrentUser = GetCurrentUserUseCase(FakeAuthStore()),
    )

    private class FakeRedemptionStore : RedemptionStore {
        private val active = code("active-1", RedemptionStatus.ACTIVE)
        private val redeemed = code("redeemed-1", RedemptionStatus.REDEEMED)
        private val expired = code("expired-1", RedemptionStatus.EXPIRED)
        var generateCalled = false

        override suspend fun generate(consumerId: String, promotion: Promotion): RedemptionCode {
            generateCalled = true
            return active
        }
        override suspend fun getByConsumer(consumerId: String): List<RedemptionCode> =
            listOf(active, redeemed, expired)
        override suspend fun getById(id: String): RedemptionCode = active
        override suspend fun confirm(code: RedemptionCode): RedemptionCode = redeemed
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

private val user = User(userId = "consumer-1", email = "user@test.com", role = "Consumer")

private fun promotion(
    endDate: Instant = Instant.now().plusSeconds(86_400),
    status: PromotionStatus = PromotionStatus.PUBLISHED,
    availableRedemptions: Int = 10,
    currentRedemptions: Int = 0,
) = Promotion(
    id = "promo-1",
    businessId = "business-1",
    title = "Promo",
    description = "Promo test",
    discountValue = 10.0,
    discountType = DiscountType.PERCENTAGE,
    status = status,
    imageUrl = null,
    imageKey = null,
    termsAndConditions = null,
    availableRedemptions = availableRedemptions,
    currentRedemptions = currentRedemptions,
    startDate = Instant.EPOCH,
    endDate = endDate,
    createdAt = Instant.EPOCH,
    updatedAt = null,
    isFavorite = false,
    category = PromotionCategory.OTHER,
    locationName = null,
    businessName = "Negocio",
    rating = null,
)

private fun code(id: String, status: RedemptionStatus) = RedemptionCode(
    id = id,
    promotionId = "promo-1",
    businessId = "business-1",
    code = id,
    token = id,
    status = status,
    discountAppliedAmount = 10.0,
    expiresAt = null,
    redeemedAt = null,
    blockedAt = null,
    businessName = "Negocio",
    promotionTitle = "Promo",
    discountValue = 10.0,
    discountType = null,
    imageKey = null,
)
