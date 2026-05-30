package com.example.klippr.core.database.converters

import androidx.room.TypeConverter
import java.time.Instant

/**
 * TypeConverter de Room para [Instant] ↔ [Long] (epoch en milisegundos).
 *
 * ### Propósito
 * Permite que Room persista campos de tipo [java.time.Instant] como enteros `Long` en SQLite.
 * Es requerido por todas las entidades que declaren columnas de fecha/hora con ese tipo
 * (actualmente [com.example.klippr.promotions.data.local.entity.PromotionEntity]).
 *
 * ### Responsabilidad
 * Conversión bidireccional sin pérdida de precisión (milisegundos UTC). No contiene lógica de
 * negocio; es infraestructura pura de la capa `core/database`.
 *
 * ### Uso
 * Registrado en [@Database][androidx.room.Database] mediante `@TypeConverters(InstantConverter::class)`.
 * Room lo aplica automáticamente a todos los campos [Instant] de las entidades registradas.
 *
 * ### Nota de compatibilidad
 * [java.time.Instant] requiere API 26+. Con `minSdk = 24` debe activarse core library desugaring
 * antes de ejecutar en dispositivos API 24/25.
 */
class InstantConverter {

    /** Convierte epoch en milisegundos a [Instant]; devuelve `null` si [value] es `null`. */
    @TypeConverter
    fun longToInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    /** Convierte [Instant] a epoch en milisegundos; devuelve `null` si [instant] es `null`. */
    @TypeConverter
    fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilli()
}
