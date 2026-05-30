package com.example.klippr.promotions.domain.model

/**
 * Estado del ciclo de vida de una promoción dentro del bounded context **Promotions**.
 *
 * ### Propósito
 * Modela, en términos de negocio, la fase en la que se encuentra una [Promotion]. Refleja el
 * `PromotionStatus` del backend DDD (que usa PascalCase: `Draft`, `Published`, `Expired`,
 * `Cancelled`); aquí se expresa con la convención Kotlin `UPPER_SNAKE_CASE`. La traducción
 * entre ambos formatos será responsabilidad del futuro mapper.
 *
 * ### Responsabilidad
 * Es un enum de dominio puro: no contiene lógica de negocio, persistencia ni UI. Permite filtrar
 * y representar promociones según su estado (por ejemplo, mostrar solo las [PUBLISHED]).
 *
 * ### Relación con el bounded context Promotions
 * Forma parte de la capa `domain/model`. En la capa de datos ([PromotionEntity]) este estado se
 * persiste como `String` para mantener la entidad Room independiente de TypeConverters de enums.
 *
 * Transiciones de negocio (definidas y validadas en el backend):
 * - [DRAFT] → [PUBLISHED] (requiere negocio verificado)
 * - [DRAFT] / [PUBLISHED] → [CANCELLED]
 * - cualquier estado activo → [EXPIRED] al vencer el periodo de validez.
 */
enum class PromotionStatus {

    /** Borrador: creada pero aún no publicada. Solo editable en este estado. */
    DRAFT,

    /** Publicada: visible y canjeable mientras esté dentro de su periodo de validez. */
    PUBLISHED,

    /** Expirada: su fecha de fin ya pasó. Transición automática. */
    EXPIRED,

    /** Cancelada: anulada manualmente antes de su expiración natural. */
    CANCELLED,
}
