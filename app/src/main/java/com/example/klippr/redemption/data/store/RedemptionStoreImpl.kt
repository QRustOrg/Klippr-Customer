package com.example.klippr.redemption.data.store

import com.example.klippr.promotions.data.network.PromotionWebService
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.data.network.RedemptionWebService
import com.example.klippr.redemption.domain.model.ConfirmRedemptionRequest
import com.example.klippr.redemption.domain.model.RedeemPromotionRequest
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.domain.model.RedemptionResource
import com.example.klippr.redemption.domain.model.RedemptionStatus
import com.example.klippr.shared.data.network.safeApiCall


// @author Samuel Bonifacio
/**
 * Implementacion de [RedemptionStore] sobre la API (sin cache local: fuente de verdad = backend).
 * Si el backend no embebe el resumen de la promo, lo enriquece via [promotionWebService]
 * (fallback fetch, cacheado por peticion). El mapeo se hace en linea, sin capa mapper aparte.
 */
class RedemptionStoreImpl(
    private val webService: RedemptionWebService,
    private val promotionWebService: PromotionWebService,
) : RedemptionStore {

    override suspend fun generate(consumerId: String, promotion: Promotion): RedemptionCode {
        val request = RedeemPromotionRequest(
            consumerId = consumerId,
            businessId = promotion.businessId,
            promotionId = promotion.id,
            expiresAt = promotion.endDate.toString(),
            discountAppliedAmount = promotion.discountValue,
            validationMethod = "QrScan",
        )
        val resource = safeApiCall { webService.generate(request) }
        // Asegura resumen de promo en la tarjeta aunque la respuesta venga minima.
        return toDomain(resource).copy(
            promotionId = promotion.id,
            businessName = promotion.businessName ?: resource.businessName,
            promotionTitle = promotion.title,
            discountValue = promotion.discountValue,
            discountType = promotion.discountType,
        )
    }

    override suspend fun getByConsumer(consumerId: String): List<RedemptionCode> =
        toDomainList(safeApiCall { webService.getByConsumer(consumerId) })

    override suspend fun getById(id: String): RedemptionCode =
        toDomain(safeApiCall { webService.getById(id) })

    override suspend fun confirm(code: RedemptionCode): RedemptionCode {
        val request = ConfirmRedemptionRequest(
            businessId = code.businessId.orEmpty(),
            confirmedAt = System.currentTimeMillis().toString(),
        )
        return toDomain(safeApiCall { webService.confirm(code.id, request) })
    }

    // ── Mapeo en linea (antes RedemptionMapper) ────────────────────────────────
    private suspend fun toDomain(
        resource: RedemptionResource,
        promoCache: MutableMap<String, PromoSummary?> = mutableMapOf(),
    ): RedemptionCode {
        val promotionId = resource.promotionId.orEmpty()
        val expiresAt = resource.expiresAt.parseInstantOrNull()
        val redeemedAt = resource.confirmedAt.parseInstantOrNull()
        val blockedAt = resource.blockedAt.parseInstantOrNull()

        val embedded = PromoSummary(
            businessName = resource.businessName,
            title = resource.promotionTitle,
            discountValue = resource.discountValue,
            discountType = resource.discountType?.toDiscountType(),
            imageKey = null,
        )
        val summary = if (embedded.isComplete || promotionId.isBlank()) {
            embedded
        } else {
            promoCache.getOrPut(promotionId) { fetchSummary(promotionId) } ?: embedded
        }

        return RedemptionCode(
            id = resource.id ?: resource.code.orEmpty(),
            promotionId = promotionId,
            businessId = resource.businessId,
            code = resource.code.orEmpty(),
            token = resource.token.orEmpty(),
            status = resolveStatus(resource.status, expiresAt, redeemedAt, blockedAt),
            discountAppliedAmount = resource.discountAppliedAmount ?: summary.discountValue ?: 0.0,
            expiresAt = expiresAt,
            redeemedAt = redeemedAt,
            blockedAt = blockedAt,
            businessName = summary.businessName,
            promotionTitle = summary.title,
            discountValue = summary.discountValue,
            discountType = summary.discountType,
            imageKey = summary.imageKey,
        )
    }

    private suspend fun toDomainList(resources: List<RedemptionResource>): List<RedemptionCode> {
        val cache = mutableMapOf<String, PromoSummary?>()
        return resources.map { toDomain(it, cache) }
    }

    private suspend fun fetchSummary(promotionId: String): PromoSummary? = try {
        val p = promotionWebService.getById(promotionId)
        PromoSummary(
            businessName = p.businessId, // el backend de promo no trae nombre; se usa id como respaldo
            title = p.title,
            discountValue = p.discountAmount,
            discountType = p.discountType.toDiscountType(),
            imageKey = p.imageKey,
        )
    } catch (e: Exception) {
        null
    }

    private data class PromoSummary(
        val businessName: String?,
        val title: String?,
        val discountValue: Double?,
        val discountType: DiscountType?,
        val imageKey: String?,
    ) {
        val isComplete: Boolean get() = title != null && discountValue != null && discountType != null
    }

    private fun resolveStatus(
        raw: String?,
        expiresAt: Long?,
        redeemedAt: Long?,
        blockedAt: Long?,
    ): RedemptionStatus {
        val normalized = raw?.trim()?.replace(Regex("[_\\-\\s]"), "")?.lowercase()
        val base = when (normalized) {
            "redeemed", "blocked", "confirmed", "used", "completed" -> RedemptionStatus.REDEEMED
            "expired", "cancelled", "canceled", "void" -> RedemptionStatus.EXPIRED
            "active", "pending", "created", "issued", "generated", null, "" -> RedemptionStatus.ACTIVE
            else -> RedemptionStatus.ACTIVE
        }
        if (base == RedemptionStatus.REDEEMED || redeemedAt != null || blockedAt != null) return RedemptionStatus.REDEEMED
        if (base == RedemptionStatus.ACTIVE && expiresAt != null && expiresAt < System.currentTimeMillis()) {
            return RedemptionStatus.EXPIRED
        }
        return base
    }

    private fun String?.parseInstantOrNull(): Long? {
        val raw = this?.takeIf { it.isNotBlank() } ?: return null
        return runCatching {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            sdf.parse(raw.substring(0, 19)).time
        }.getOrNull()
    }

    private fun String.toDiscountType(): DiscountType = when (
        trim().replace(Regex("[_\\-\\s]"), "").lowercase()
    ) {
        "fixedamount", "fixed", "amount" -> DiscountType.FIXED_AMOUNT
        else -> DiscountType.PERCENTAGE
    }
}
