package com.example.klippr.promotions.data.repository

import com.example.klippr.promotions.data.local.dao.PromotionDao
import com.example.klippr.promotions.data.mapper.toDomain
import com.example.klippr.promotions.data.mapper.toEntity
import com.example.klippr.promotions.data.remote.api.PromotionApiService
import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import com.example.klippr.promotions.domain.repository.PromotionRepository
import com.example.klippr.core.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// @author Samuel Bonifacio
// Implementacion de PromotionRepository que combina acceso a datos local (Room) y remoto (Retrofit).
// Maneja mapeo entre entidades, DTOs y modelos de dominio, y preserva el estado de favoritos.
// Al refrescar datos desde la API.
class PromotionRepositoryImpl(
    private val dao: PromotionDao,
    private val api: PromotionApiService,
) : PromotionRepository {

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
        // Preserva isFavorite local al reemplazar la caché con datos frescos de la API.
        val favMap = dao.getAll().first().associate { it.id to it.isFavorite }
        val entities = safeApiCall { api.getAll() }
            .map { dto -> dto.toEntity(isFavorite = favMap[dto.id] ?: false) }
        dao.upsertAll(entities)
    }

    override suspend fun refreshActive() {
        // Preserva isFavorite local al reemplazar la caché con datos frescos de la API.
        val favMap = dao.getAll().first().associate { it.id to it.isFavorite }
        val entities = safeApiCall { api.getActive() }
            .map { dto -> dto.toEntity(isFavorite = favMap[dto.id] ?: false) }
        dao.upsertAll(entities)
    }

    override suspend fun refreshByBusiness(businessId: String) {
        val favMap = dao.getByBusinessId(businessId).first().associate { it.id to it.isFavorite }
        val entities = safeApiCall { api.getByBusiness(businessId) }
            .map { dto -> dto.toEntity(isFavorite = favMap[dto.id] ?: false) }
        dao.upsertAll(entities)
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) =
        dao.updateFavorite(id, isFavorite)
}
