package com.example.klippr.promotions.domain.model

import java.time.Instant

/**
 * Modelo de dominio de una **promoción**, núcleo del bounded context **Promotions**.
 *
 * ### Propósito
 * Representa una promoción tal como la entiende el negocio y la consume la aplicación: es el
 * *read/display model* que alimenta el listado, el detalle, la búsqueda y los filtros. Es un
 * modelo **puro**: no conoce Room, Retrofit, Compose ni ningún detalle de infraestructura.
 *
 * ### Responsabilidad
 * Ser la representación inmutable y framework-agnóstica de una promoción dentro de la capa
 * `domain`. No contiene lógica de negocio, mapeos ni efectos secundarios; solo datos.
 *
 * ### Relación con el bounded context Promotions
 * Es la fuente de verdad de dominio. La capa de datos lo persiste/recupera a través de
 * [PromotionEntity] (caché local Room) y de DTOs remotos, mediante mappers que se implementarán
 * por separado.
 *
 * ### Nota sobre el contrato con el backend (DDD)
 * El agregado `Promotion` del backend define únicamente: `id`, `businessId`, `title`,
 * `description`, descuento (valor + tipo), periodo de validez (`startDate`/`endDate`),
 * `redemptionCap`, `status`, `createdAt` y `updatedAt`. Los campos adicionales de este modelo
 * ([imageUrl], [termsAndConditions], [currentRedemptions], [category], [locationName],
 * [businessName], [rating], [isFavorite]) son enriquecimientos de presentación o provienen de
 * otros bounded contexts (Profile, Redemption, Favorites). Se incluyen aquí de forma intencional
 * para soportar la UI offline-first.
 *
 * @property id Identificador único de la promoción.
 * @property businessId Identificador del negocio propietario (referencia al Profile BC).
 * @property title Título comercial de la promoción.
 * @property description Descripción visible para el usuario.
 * @property discountValue Valor numérico del descuento; su interpretación depende de [discountType].
 * @property discountType Tipo de descuento aplicado ([DiscountType.PERCENTAGE] o
 *   [DiscountType.FIXED_AMOUNT]).
 * @property status Estado actual dentro del ciclo de vida ([PromotionStatus]).
 * @property imageUrl URL de la imagen de la promoción; `null` si no tiene.
 * @property termsAndConditions Términos y condiciones; `null` si no aplican.
 * @property availableRedemptions Cupo máximo de canjes disponibles para la promoción.
 * @property currentRedemptions Cantidad de canjes ya realizados (origen: Redemption BC).
 * @property startDate Inicio del periodo de validez (UTC).
 * @property endDate Fin del periodo de validez (UTC).
 * @property createdAt Marca temporal de creación (UTC).
 * @property updatedAt Marca temporal de última modificación (UTC); `null` si nunca se modificó.
 * @property isFavorite Indica si el usuario actual la marcó como favorita (origen: Favorites BC).
 * @property category Categoría temática para filtros y búsqueda ([PromotionCategory]).
 * @property locationName Nombre legible de la ubicación; `null` si no está disponible.
 * @property businessName Nombre del negocio para mostrar en tarjetas; `null` si no está cargado.
 * @property rating Valoración del negocio/promoción; `null` si no está disponible.
 */
data class Promotion(
    val id: String,
    val businessId: String,
    val title: String,
    val description: String,
    val discountValue: Double,
    val discountType: DiscountType,
    val status: PromotionStatus,
    val imageUrl: String?,
    val termsAndConditions: String?,
    val availableRedemptions: Int,
    val currentRedemptions: Int,
    val startDate: Instant,
    val endDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val isFavorite: Boolean,
    val category: PromotionCategory,
    val locationName: String?,
    val businessName: String?,
    val rating: Double?,
)
