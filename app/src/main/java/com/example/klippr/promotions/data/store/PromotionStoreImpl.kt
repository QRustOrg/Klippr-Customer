package com.example.klippr.promotions.data.store

import com.example.klippr.profile.data.network.ProfileWebService
import com.example.klippr.promotions.data.local.dao.PromotionDao
import com.example.klippr.promotions.data.local.entity.PromotionEntity
import com.example.klippr.promotions.data.network.PromotionWebService
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionResource
import com.example.klippr.promotions.domain.model.PromotionStatus
import com.example.klippr.shared.data.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

// @author Samuel Bonifacio
/**
 * Implementacion de [PromotionStore]: combina cache local (Room) y backend (Retrofit).
 * Preserva el estado de favoritos al refrescar. El mapeo recurso/entidad/dominio se hace en linea.
 */
class PromotionStoreImpl(
    private val dao: PromotionDao,
    private val webService: PromotionWebService,
    private val profileWebService: ProfileWebService,
) : PromotionStore {

    override fun getAll(): Flow<List<Promotion>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Promotion? =
        dao.getById(id)?.toDomain()

    override fun getByStatus(status: PromotionStatus): Flow<List<Promotion>> =
        dao.getByStatus(status.name).map { list -> list.map { it.toDomain() } }

    override fun getByCategory(category: PromotionCategory): Flow<List<Promotion>> =
        dao.getByCategory(category.name).map { list -> list.map { it.toDomain() } }

    override fun getFavorites(): Flow<List<Promotion>> =
        dao.getFavorites().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Promotion>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override fun getByStatusAndCategory(
        status: PromotionStatus,
        category: PromotionCategory,
    ): Flow<List<Promotion>> =
        dao.getByStatusAndCategory(status.name, category.name)
            .map { list -> list.map { it.toDomain() } }

    override fun getByBusinessId(businessId: String): Flow<List<Promotion>> =
        dao.getByBusinessId(businessId).map { list -> list.map { it.toDomain() } }

    override suspend fun refreshAll() {
        val favMap = dao.getAll().first().associate { it.id to it.isFavorite }
        val entities = safeApiCall { webService.getAll() }
            .map { resource -> resource.toEntity(isFavorite = favMap[resource.id] ?: false) }
        dao.upsertAll(entities)
    }

    override suspend fun refreshActive() {
        val favMap = dao.getAll().first().associate { it.id to it.isFavorite }
        val entities = safeApiCall { webService.getActive() }
            .map { resource -> resource.toEntity(isFavorite = favMap[resource.id] ?: false) }
        dao.upsertAll(entities)
    }

    override suspend fun refreshByBusiness(businessId: String) {
        val favMap = dao.getByBusinessId(businessId).first().associate { it.id to it.isFavorite }
        val entities = safeApiCall { webService.getByBusiness(businessId) }
            .map { resource -> resource.toEntity(isFavorite = favMap[resource.id] ?: false) }
        dao.upsertAll(entities)
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) =
        dao.updateFavorite(id, isFavorite)

    // El businessId de la promocion es el userId del negocio: GET /api/Users/{id}.businessName.
    override suspend fun getBusinessName(businessId: String): String? =
        runCatching { profileWebService.getUser(businessId).businessName }.getOrNull()?.takeIf { it.isNotBlank() }

    // ── Mapeo en linea ────────────────────────────────────────────────────────
    private fun PromotionResource.toEntity(isFavorite: Boolean = false): PromotionEntity = PromotionEntity(
        id = id,
        businessId = businessId,
        title = title,
        description = description,
        discountValue = discountAmount,
        discountType = discountType.toKotlinDiscountType().name,
        status = status.toKotlinPromotionStatus().name,
        imageUrl = null,
        imageKey = imageKey,
        termsAndConditions = termsAndConditions?.takeIf { it.isNotBlank() },
        availableRedemptions = redemptionCap ?: Int.MAX_VALUE,
        currentRedemptions = currentRedemptions ?: 0,
        startDate = startDate.toInstantFlexible(),
        endDate = endDate.toInstantFlexible(),
        createdAt = createdAt.toInstantFlexible(),
        updatedAt = updatedAt.takeIf { it.isNotBlank() }?.toInstantFlexible(),
        isFavorite = isFavorite,
        category = (category.toKotlinPromotionCategoryOrNull() ?: imageKey.toCategoryFromImageKey()).name,
        locationName = locationName?.takeIf { it.isNotBlank() },
        businessName = businessName?.takeIf { it.isNotBlank() },
        rating = rating,
    )

    private fun PromotionEntity.toDomain(): Promotion = Promotion(
        id = id,
        businessId = businessId,
        title = title,
        description = description,
        discountValue = discountValue,
        discountType = DiscountType.valueOf(discountType),
        status = PromotionStatus.valueOf(status),
        imageUrl = imageUrl,
        imageKey = imageKey,
        termsAndConditions = termsAndConditions,
        availableRedemptions = availableRedemptions,
        currentRedemptions = currentRedemptions,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isFavorite = isFavorite,
        category = PromotionCategory.valueOf(category),
        locationName = locationName,
        businessName = businessName,
        rating = rating,
    )

    // El backend envia timestamps sin zona ("2026-06-15T14:54:57.654618"); cae a UTC.
    private fun String.toInstantFlexible(): Instant =
        runCatching { Instant.parse(this) }
            .getOrElse { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC) }

    private fun String.toKotlinDiscountType(): DiscountType = when (
        trim().replace(Regex("[_\\-\\s]"), "").lowercase()
    ) {
        "percentage", "percent" -> DiscountType.PERCENTAGE
        "fixedamount", "fixed", "amount" -> DiscountType.FIXED_AMOUNT
        else -> DiscountType.PERCENTAGE
    }

    private fun String.toKotlinPromotionStatus(): PromotionStatus = when (trim().lowercase()) {
        "draft" -> PromotionStatus.DRAFT
        "published" -> PromotionStatus.PUBLISHED
        "expired" -> PromotionStatus.EXPIRED
        "cancelled" -> PromotionStatus.CANCELLED
        else -> PromotionStatus.DRAFT
    }

    private fun String?.toKotlinPromotionCategoryOrNull(): PromotionCategory? = when (
        this?.trim()?.replace(Regex("[_\\-\\s]"), "")?.lowercase()
    ) {
        "food", "comida" -> PromotionCategory.FOOD
        "beauty", "belleza" -> PromotionCategory.BEAUTY
        "health", "salud" -> PromotionCategory.HEALTH
        "education", "educacion", "educaci\u00f3n" -> PromotionCategory.EDUCATION
        "entertainment", "entretenimiento" -> PromotionCategory.ENTERTAINMENT
        "sports", "deportes" -> PromotionCategory.SPORTS
        "services", "servicios" -> PromotionCategory.SERVICES
        "technology", "tecnologia", "tecnolog\u00eda" -> PromotionCategory.TECHNOLOGY
        "other", "otros" -> PromotionCategory.OTHER
        else -> null
    }

    private fun String?.toCategoryFromImageKey(): PromotionCategory {
        val key = this?.lowercase().orEmpty()
        return when {
            key.isBlank() -> PromotionCategory.OTHER
            key.startsWith("comida_") -> PromotionCategory.FOOD
            key.startsWith("salud_") -> PromotionCategory.HEALTH
            key.startsWith("entretenimiento_") -> PromotionCategory.ENTERTAINMENT
            key.startsWith("deportes_") -> PromotionCategory.SPORTS
            else -> PromotionCategory.OTHER
        }
    }
}
