package com.example.klippr.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.klippr.community.data.local.dao.ReviewDao
import com.example.klippr.community.data.local.entity.ReviewEntity
import com.example.klippr.core.database.converters.InstantConverter
import com.example.klippr.promotions.data.local.dao.PromotionDao
import com.example.klippr.promotions.data.local.entity.PromotionEntity

// @author Samuel Bonifacio
// exportSchema = true para historial de migraciones. Instanciar como singleton vía DI.
@Database(
    entities = [
        PromotionEntity::class,
        ReviewEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(InstantConverter::class)
abstract class KlipprDatabase : RoomDatabase() {
    abstract fun promotionDao(): PromotionDao
    abstract fun reviewDao(): ReviewDao
}