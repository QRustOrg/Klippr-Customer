package com.example.klippr.promotions.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

// @author Samuel Bonifacio
// Enums se guardan como String; Instant requiere InstantConverter registrado en KlipprDatabase.
@Entity(
    tableName = "promotions",
    indices = [
        Index(value = ["businessId"]),
        Index(value = ["status"]),
        Index(value = ["category"]),
        Index(value = ["isFavorite"]),
        Index(value = ["startDate"]),
        Index(value = ["endDate"]),
    ],
)

//Entidad Principal de Promotion con sus datos necesarios para la persistencia local.
//Incluye campos para detalles de la promoción, estado, fechas, y relaciones con el negocio.
data class PromotionEntity(
    @PrimaryKey val id: String,
    val businessId: String,
    val title: String,
    val description: String,
    val discountValue: Double,
    val discountType: String,
    val status: String,
    val imageUrl: String?,
    val imageKey: String?,
    val termsAndConditions: String?,
    val availableRedemptions: Int,
    val currentRedemptions: Int,
    val startDate: Instant,
    val endDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val isFavorite: Boolean,
    val category: String,
    val locationName: String?,
    val businessName: String?,
    val rating: Double?,
)
