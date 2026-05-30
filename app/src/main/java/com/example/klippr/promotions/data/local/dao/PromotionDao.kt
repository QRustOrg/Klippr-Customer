package com.example.klippr.promotions.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.klippr.promotions.data.local.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Room para el bounded context **Promotions**.
 *
 * ### Propósito
 * Define el contrato de acceso a la tabla `promotions` de la base de datos local.
 * Soporta las operaciones necesarias para: listado, detalle, búsqueda por texto, filtros
 * (estado, categoría), favoritos y gestión de la caché offline.
 *
 * ### Responsabilidad
 * Capa de persistencia local pura. No contiene lógica de negocio ni transformaciones de dominio;
 * eso es responsabilidad de los mappers y repositorios correspondientes.
 *
 * ### Estrategia reactiva
 * Las queries de lectura devuelven [Flow] para que la UI reaccione automáticamente ante cambios
 * en la caché local. Las operaciones de escritura son `suspend` para ejecutarse en coroutines.
 *
 * ### Relación con el bounded context Promotions
 * Pertenece a `promotions/data/local/dao`. Opera exclusivamente sobre [PromotionEntity] y es
 * consumido por el futuro `PromotionRepositoryImpl`.
 */
@Dao
interface PromotionDao {

    // ── Lectura ──────────────────────────────────────────────────────────────────────────────────

    /** Devuelve todas las promociones cacheadas, ordenadas por fecha de inicio descendente. */
    @Query("SELECT * FROM promotions ORDER BY startDate DESC")
    fun getAll(): Flow<List<PromotionEntity>>

    /** Devuelve la promoción con el [id] indicado, o `null` si no existe en caché. */
    @Query("SELECT * FROM promotions WHERE id = :id")
    suspend fun getById(id: String): PromotionEntity?

    /**
     * Filtra promociones por [status] (valor `String`, e.g. `"PUBLISHED"`),
     * ordenadas por fecha de inicio descendente.
     */
    @Query("SELECT * FROM promotions WHERE status = :status ORDER BY startDate DESC")
    fun getByStatus(status: String): Flow<List<PromotionEntity>>

    /**
     * Filtra promociones por [category] (valor `String`, e.g. `"FOOD"`),
     * ordenadas por fecha de inicio descendente.
     */
    @Query("SELECT * FROM promotions WHERE category = :category ORDER BY startDate DESC")
    fun getByCategory(category: String): Flow<List<PromotionEntity>>

    /** Devuelve las promociones marcadas como favoritas por el usuario. */
    @Query("SELECT * FROM promotions WHERE isFavorite = 1 ORDER BY startDate DESC")
    fun getFavorites(): Flow<List<PromotionEntity>>

    /**
     * Busca promociones cuyo [title] o [description] contengan [query] (búsqueda de texto libre,
     * insensible a mayúsculas).
     */
    @Query("""
        SELECT * FROM promotions
        WHERE title LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY startDate DESC
    """)
    fun search(query: String): Flow<List<PromotionEntity>>

    /**
     * Filtra por [status] y [category] simultáneamente.
     * Útil para el panel de listado con múltiples filtros activos.
     */
    @Query("""
        SELECT * FROM promotions
        WHERE status = :status AND category = :category
        ORDER BY startDate DESC
    """)
    fun getByStatusAndCategory(status: String, category: String): Flow<List<PromotionEntity>>

    /** Devuelve las promociones de un negocio concreto, ordenadas por fecha de inicio. */
    @Query("SELECT * FROM promotions WHERE businessId = :businessId ORDER BY startDate DESC")
    fun getByBusinessId(businessId: String): Flow<List<PromotionEntity>>

    // ── Escritura ─────────────────────────────────────────────────────────────────────────────────

    /**
     * Inserta o actualiza (upsert) una lista de promociones en la caché.
     * Estrategia: reemplaza la fila existente si el `id` ya existe.
     */
    @Upsert
    suspend fun upsertAll(entities: List<PromotionEntity>)

    /** Inserta o actualiza una sola promoción. */
    @Upsert
    suspend fun upsert(entity: PromotionEntity)

    /** Elimina la promoción indicada de la caché. */
    @Delete
    suspend fun delete(entity: PromotionEntity)

    /** Borra todas las promociones de la caché (útil para refresh completo o logout). */
    @Query("DELETE FROM promotions")
    suspend fun deleteAll()

    /** Actualiza únicamente la marca de favorito de una promoción por su [id]. */
    @Query("UPDATE promotions SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}
