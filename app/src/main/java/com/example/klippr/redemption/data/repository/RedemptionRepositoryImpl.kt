package com.example.klippr.redemption.data.repository

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.data.mapper.RedemptionMapper
import com.example.klippr.redemption.data.remote.api.RedemptionApiService
import com.example.klippr.redemption.data.remote.dto.RedeemPromotionRequestDto
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.repository.RedemptionRepository

// @author Samuel Bonifacio
/** Implementación de Redemption sobre la API (sin caché local: fuente de verdad = backend). */
class RedemptionRepositoryImpl(
    private val api: RedemptionApiService,
    private val mapper: RedemptionMapper,
) : RedemptionRepository {

    override suspend fun generate(consumerId: String, promotion: Promotion): RedemptionCode {
        val request = RedeemPromotionRequestDto(
            consumerId = consumerId,
            businessId = promotion.businessId,
            promotionId = promotion.id,
            expiresAt = promotion.endDate.toString(),
            discountAppliedAmount = promotion.discountValue,
            validationMethod = "QrScan",
        )
        val dto = api.generate(request)
        // Asegura resumen de promo en la tarjeta aunque la respuesta venga mínima.
        return mapper.toDomain(dto).copy(
            promotionId = promotion.id,
            businessName = promotion.businessName ?: dto.businessName,
            promotionTitle = promotion.title,
            discountValue = promotion.discountValue,
            discountType = promotion.discountType,
        )
    }

    override suspend fun getByConsumer(consumerId: String): List<RedemptionCode> =
        mapper.toDomainList(api.getByConsumer(consumerId))

    override suspend fun getById(id: String): RedemptionCode =
        mapper.toDomain(api.getById(id))
}
