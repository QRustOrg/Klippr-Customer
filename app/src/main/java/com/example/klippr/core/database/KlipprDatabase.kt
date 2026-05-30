package com.example.klippr.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.klippr.core.database.converters.InstantConverter
import com.example.klippr.promotions.data.local.dao.PromotionDao
import com.example.klippr.promotions.data.local.entity.PromotionEntity

/**
 * Base de datos Room de la aplicación Klippr.
 *
 * ### Propósito
 * Punto de entrada único a la persistencia local (SQLite vía Room). Registra todas las entidades
 * y TypeConverters de la aplicación, y expone los DAOs de cada bounded context.
 *
 * ### Responsabilidad
 * Infraestructura pura de `core/database`. No contiene lógica de negocio. Se instancia como
 * **singleton** (responsabilidad del futuro módulo de inyección de dependencias).
 *
 * ### Estrategia de migraciones
 * `exportSchema = true` para mantener el historial de esquema en `assets/` y facilitar
 * migraciones futuras. En desarrollo se puede usar `fallbackToDestructiveMigration()` si la
 * migración aún no está escrita; en producción se debe proveer una [Migration] explícita.
 *
 * ### Agregar nuevas entidades
 * 1. Añadir la clase al array [entities].
 * 2. Incrementar [version].
 * 3. Proveer una [androidx.room.migration.Migration] o habilitar destructive migration.
 * 4. Exponer el DAO correspondiente como función abstracta.
 */
@Database(
    entities = [
        PromotionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class KlipprDatabase : RoomDatabase() {

    /** DAO para el bounded context Promotions. */
    abstract fun promotionDao(): PromotionDao
}
