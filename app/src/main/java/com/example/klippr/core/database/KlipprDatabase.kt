package com.example.klippr.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.klippr.core.database.converters.InstantConverter
import com.example.klippr.promotions.data.local.dao.PromotionDao
import com.example.klippr.promotions.data.local.entity.PromotionEntity

// @author Samuel Bonifacio
// exportSchema = true para historial de migraciones. Instanciar como singleton vía DI.
@Database(
    entities = [PromotionEntity::class],
    // v2: se agregó la columna imageKey a PromotionEntity. El bump de versión permite que
    // fallbackToDestructiveMigration recree la tabla (sin él, Room lanza IllegalStateException
    // por hash de esquema distinto a la misma versión).
    version = 2,
    exportSchema = false,
)
@TypeConverters(InstantConverter::class)
abstract class KlipprDatabase : RoomDatabase() {
    abstract fun promotionDao(): PromotionDao
}
