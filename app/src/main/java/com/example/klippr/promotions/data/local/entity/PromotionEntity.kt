package com.example.klippr.promotions.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entidad Room que representa la tabla local **`promotions`** del bounded context **Promotions**.
 *
 * ### Propósito
 * Es la representación persistible de una promoción para la **caché local** (estrategia
 * offline-first). Modela exactamente las columnas de la tabla `promotions` y existe únicamente en
 * la capa de datos.
 *
 * ### Responsabilidad
 * Persistencia local con Room. No contiene lógica de negocio ni de UI. La conversión entre esta
 * entidad y el modelo de dominio `Promotion` se hará en un mapper independiente (no incluido aún).
 *
 * ### Relación con el bounded context Promotions
 * Es el espejo, en la capa `data/local`, del modelo de dominio `Promotion`. Soporta listado,
 * detalle, búsqueda, filtros y los futuros favoritos/redemptions sobre datos cacheados.
 *
 * ### Decisiones de mapeo a tipos Room
 * - **Enums como `String`**: `discountType`, `status` y `category` se almacenan como `String` para
 *   mantener la entidad libre de TypeConverters de enums. El mapper traducirá `String` ↔ enum.
 * - **Fechas con TypeConverter**: `startDate`, `endDate`, `createdAt` y `updatedAt` son
 *   [java.time.Instant]. La entidad queda **preparada** para un TypeConverter `Instant` ↔ `Long`
 *   que aún **no** se ha creado; Room exigirá dicho converter cuando esta entidad se registre en
 *   un `@Database`.
 *
 * ### Índices
 * Se indexan las columnas usadas en consultas frecuentes (filtros y rangos): [businessId],
 * [status], [category], [isFavorite], [startDate] y [endDate].
 *
 * @property id Clave primaria; identificador único de la promoción.
 * @property businessId Identificador del negocio propietario. Indexado.
 * @property title Título comercial.
 * @property description Descripción visible.
 * @property discountValue Valor numérico del descuento.
 * @property discountType Tipo de descuento, almacenado como `String`.
 * @property status Estado del ciclo de vida, almacenado como `String`. Indexado.
 * @property imageUrl URL de la imagen; `null` si no tiene.
 * @property termsAndConditions Términos y condiciones; `null` si no aplican.
 * @property availableRedemptions Cupo máximo de canjes disponibles.
 * @property currentRedemptions Cantidad de canjes ya realizados.
 * @property startDate Inicio del periodo de validez (requiere TypeConverter). Indexado.
 * @property endDate Fin del periodo de validez (requiere TypeConverter). Indexado.
 * @property createdAt Marca temporal de creación (requiere TypeConverter).
 * @property updatedAt Marca temporal de última modificación (requiere TypeConverter); `null` si
 *   nunca se modificó.
 * @property isFavorite Marca de favorito del usuario actual. Indexado.
 * @property category Categoría temática, almacenada como `String`. Indexado.
 * @property locationName Nombre legible de la ubicación; `null` si no disponible.
 * @property businessName Nombre del negocio; `null` si no cargado.
 * @property rating Valoración; `null` si no disponible.
 */
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
data class PromotionEntity(
    @PrimaryKey val id: String,
    val businessId: String,
    val title: String,
    val description: String,
    val discountValue: Double,
    val discountType: String,
    val status: String,
    val imageUrl: String?,
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
