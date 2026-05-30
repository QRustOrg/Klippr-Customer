package com.example.klippr.promotions.domain.model

/**
 * Tipo de descuento aplicado por una promoción dentro del bounded context **Promotions**.
 *
 * ### Propósito
 * Define cómo debe interpretarse el valor numérico del descuento de una [Promotion]
 * (`discountValue`). Refleja el `DiscountType` del value object `DiscountValue` del backend DDD
 * (PascalCase: `Percentage`, `FixedAmount`), expresado aquí en `UPPER_SNAKE_CASE`.
 *
 * ### Responsabilidad
 * Enum de dominio puro, sin lógica de negocio ni validaciones. La validación del rango del valor
 * (por ejemplo, 0–100 para [PERCENTAGE]) reside en el backend y, de ser necesaria, en futuras
 * capas de dominio del frontend.
 *
 * ### Relación con el bounded context Promotions
 * Pertenece a `domain/model`. En [PromotionEntity] se persiste como `String`, manteniendo la
 * entidad Room libre de TypeConverters de enums.
 */
enum class DiscountType {

    /** Descuento porcentual: el valor representa un porcentaje (típicamente 0–100). */
    PERCENTAGE,

    /** Descuento de monto fijo: el valor representa una cantidad concreta a descontar. */
    FIXED_AMOUNT,
}
