package com.example.klippr.redemption.data.store

import com.example.klippr.promotions.data.network.PromotionWebService
import com.example.klippr.promotions.domain.model.PromotionResource
import com.example.klippr.redemption.data.network.RedemptionWebService
import com.example.klippr.redemption.domain.model.ConfirmRedemptionRequest
import com.example.klippr.redemption.domain.model.RedeemPromotionRequest
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionResource
import com.example.klippr.redemption.domain.model.RedemptionStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.Instant

class RedemptionStoreImplTest {

    @Test
    fun confirm_sendsConfirmedAtAsIsoInstant() = runBlocking {
        val webService = CapturingRedemptionWebService()
        val store = RedemptionStoreImpl(webService, NoopPromotionWebService())

        store.confirm(code())

        val request = webService.confirmRequest
        assertNotNull(request)
        Instant.parse(request!!.confirmedAt)
        assertEquals("ManualCode", request.validationMethod)
    }

    private class CapturingRedemptionWebService : RedemptionWebService {
        var confirmRequest: ConfirmRedemptionRequest? = null

        override suspend fun generate(body: RedeemPromotionRequest): RedemptionResource =
            error("Not needed for this test.")

        override suspend fun confirm(id: String, body: ConfirmRedemptionRequest): RedemptionResource {
            confirmRequest = body
            return redemptionResource(id = id, confirmedAt = body.confirmedAt)
        }

        override suspend fun getById(id: String): RedemptionResource =
            error("Not needed for this test.")

        override suspend fun getByConsumer(consumerId: String): List<RedemptionResource> =
            error("Not needed for this test.")
    }

    private class NoopPromotionWebService : PromotionWebService {
        override suspend fun getAll(): List<PromotionResource> = emptyList()
        override suspend fun getActive(): List<PromotionResource> = emptyList()
        override suspend fun getById(id: String): PromotionResource =
            error("Promotion fetch should not be needed for complete redemption resources.")

        override suspend fun getByBusiness(businessId: String): List<PromotionResource> = emptyList()
    }
}

private fun code() = RedemptionCode(
    id = "1",
    promotionId = "promo-1",
    businessId = "11111111-1111-1111-1111-111111111111",
    code = "CODE1",
    token = "22222222-2222-2222-2222-222222222222",
    status = RedemptionStatus.ACTIVE,
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

private fun redemptionResource(id: String, confirmedAt: String) = RedemptionResource(
    id = id,
    code = "CODE1",
    token = "22222222-2222-2222-2222-222222222222",
    status = "Redeemed",
    promotionId = "promo-1",
    businessId = "11111111-1111-1111-1111-111111111111",
    consumerId = "33333333-3333-3333-3333-333333333333",
    expiresAt = "2026-07-02T12:00:00Z",
    confirmedAt = confirmedAt,
    blockedAt = null,
    discountAppliedAmount = 10.0,
    businessName = "Negocio",
    promotionTitle = "Promo",
    discountValue = 10.0,
    discountType = "Percentage",
)
