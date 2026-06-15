package com.example.klippr.promotions.domain.repository

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.promotions.domain.model.PromotionStatus
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
/** Contrato de acceso a datos del BC Promotions. Implementado en data/repository. */
interface PromotionRepository {

    fun getAll(): Flow<List<Promotion>>
    suspend fun getById(id: String): Promotion?
    fun getByStatus(status: PromotionStatus): Flow<List<Promotion>>
    fun getByCategory(category: PromotionCategory): Flow<List<Promotion>>
    fun getFavorites(): Flow<List<Promotion>>
    fun search(query: String): Flow<List<Promotion>>
    fun getByStatusAndCategory(status: PromotionStatus, category: PromotionCategory): Flow<List<Promotion>>
    fun getByBusinessId(businessId: String): Flow<List<Promotion>>

    // Refresca desde la API y persiste en caché; lanza excepción si no hay red y caché está vacía.
    suspend fun refreshAll()
    suspend fun refreshActive()
    suspend fun refreshByBusiness(businessId: String)
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)

    // Resuelve el nombre del negocio (businessId == userId del negocio) vía GET /api/Users/{id}.
    // Devuelve null si no hay nombre o la llamada falla.
    suspend fun getBusinessName(businessId: String): String?
}
