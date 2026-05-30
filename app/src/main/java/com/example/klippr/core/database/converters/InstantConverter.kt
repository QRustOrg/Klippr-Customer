package com.example.klippr.core.database.converters

import androidx.room.TypeConverter
import java.time.Instant

// @author Samuel Bonifacio
// Room no soporta Instant nativamente; se serializa como epoch en milisegundos (Long).
class InstantConverter {

    @TypeConverter
    fun longToInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilli()
}
