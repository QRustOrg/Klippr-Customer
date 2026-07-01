package com.example.klippr.redemption.data.mapper

import com.example.klippr.promotions.data.remote.api.PromotionApiService
import com.example.klippr.promotions.data.remote.dto.PromotionDto
import com.example.klippr.redemption.data.remote.dto.RedemptionDto
import com.example.klippr.redemption.domain.model.RedemptionStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RedemptionMapperTest {

    private val mapper = RedemptionMapper(NoopPromotionApiService())

    @Test
    fun toDomain_mapsUniqueTokenAsQrContent() = runBlocking {
        val result = mapper.toDomain(redemptionDto(status = "Generated", token = "opaque-token"))

        assertEquals("opaque-token", result.token)
        assertEquals("opaque-token", result.qrContent)
    }

    @Test
    fun toDomain_mapsBlockedStatusAsRedeemed() = runBlocking {
        val result = mapper.toDomain(redemptionDto(status = "Blocked", blockedAt = "2026-06-24T12:00:00Z"))

        assertEquals(RedemptionStatus.REDEEMED, result.status)
    }

    @Test
    fun toDomain_fetchesPromotionImageKeyWhenRedemptionOmitsIt() = runBlocking {
        val result = RedemptionMapper(ImagePromotionApiService()).toDomain(redemptionDto(status = "Generated"))

        assertEquals("comida_ceviche", result.imageKey)
    }

    private fun redemptionDto(
        status: String,
        token: String = "token-1",
        blockedAt: String? = null,
    ) = RedemptionDto(
        id = "1",
        code = "CODE1",
        token = token,
        status = status,
        promotionId = "promo-1",
        businessId = "business-1",
        consumerId = "consumer-1",
        expiresAt = "2026-06-25T12:00:00Z",
        confirmedAt = null,
        blockedAt = blockedAt,
        discountAppliedAmount = 10.0,
        businessName = "Pizza Hot",
        promotionTitle = "Promo",
        discountValue = 10.0,
        discountType = "Percentage",
        imageKey = null,
    )

    private class NoopPromotionApiService : PromotionApiService {
        override suspend fun getAll(): List<PromotionDto> = emptyList()
        override suspend fun getActive(): List<PromotionDto> = emptyList()
        override suspend fun getById(id: String): PromotionDto {
            error("Promotion fetch should not be needed for complete redemption DTOs.")
        }
        override suspend fun getByBusiness(businessId: String): List<PromotionDto> = emptyList()
    }

    private class ImagePromotionApiService : PromotionApiService {
        override suspend fun getAll(): List<PromotionDto> = emptyList()
        override suspend fun getActive(): List<PromotionDto> = emptyList()
        override suspend fun getByBusiness(businessId: String): List<PromotionDto> = emptyList()
        override suspend fun getById(id: String): PromotionDto = PromotionDto(
            id = id,
            businessId = "business-1",
            title = "Promo",
            description = "Promo",
            discountAmount = 10.0,
            discountType = "Percentage",
            startDate = "2026-06-01T12:00:00Z",
            endDate = "2026-06-25T12:00:00Z",
            redemptionCap = 10,
            imageKey = "comida_ceviche",
            status = "Published",
            createdAt = "2026-06-01T12:00:00Z",
            updatedAt = "2026-06-01T12:00:00Z",
            isActive = true,
        )
    }
}
