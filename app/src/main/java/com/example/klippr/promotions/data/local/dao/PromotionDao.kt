package com.example.klippr.promotions.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.klippr.promotions.data.local.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow

// @author Samuel Bonifacio
/** Acceso a la tabla `promotions`. Lecturas como Flow; escrituras suspend. */
@Dao
interface PromotionDao {

    @Query("SELECT * FROM promotions ORDER BY startDate DESC")
    fun getAll(): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE id = :id")
    suspend fun getById(id: String): PromotionEntity?

    @Query("SELECT * FROM promotions WHERE status = :status ORDER BY startDate DESC")
    fun getByStatus(status: String): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE category = :category ORDER BY startDate DESC")
    fun getByCategory(category: String): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE isFavorite = 1 ORDER BY startDate DESC")
    fun getFavorites(): Flow<List<PromotionEntity>>

    // LIKE con concatenación SQL porque Room no acepta wildcards embebidos en parámetros.
    @Query("""
        SELECT * FROM promotions
        WHERE title LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY startDate DESC
    """)
    fun search(query: String): Flow<List<PromotionEntity>>

    @Query("""
        SELECT * FROM promotions
        WHERE status = :status AND category = :category
        ORDER BY startDate DESC
    """)
    fun getByStatusAndCategory(status: String, category: String): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE businessId = :businessId ORDER BY startDate DESC")
    fun getByBusinessId(businessId: String): Flow<List<PromotionEntity>>

    @Upsert
    suspend fun upsertAll(entities: List<PromotionEntity>)

    @Upsert
    suspend fun upsert(entity: PromotionEntity)

    @Delete
    suspend fun delete(entity: PromotionEntity)

    @Query("DELETE FROM promotions")
    suspend fun deleteAll()

    @Query("UPDATE promotions SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}
