package com.example.klippr.promotions.domain.model

/**
 * Categoría temática de una promoción dentro del bounded context **Promotions**.
 *
 * ### Propósito
 * Clasifica una [Promotion] por rubro para soportar **filtros** y **búsqueda** en el listado.
 * Es un campo de presentación/denormalizado del frontend: en el backend la categoría pertenece
 * al Profile BC (categoría del negocio), por lo que aquí se mantiene como dato enriquecido de la
 * caché local.
 *
 * ### Responsabilidad
 * Enum de dominio puro. No contiene lógica de negocio, persistencia ni UI.
 *
 * ### Relación con el bounded context Promotions
 * Pertenece a `domain/model`. En [PromotionEntity] se persiste como `String` y está indexado para
 * permitir filtros eficientes por categoría.
 */
enum class PromotionCategory {

    /** Comida y restaurantes. */
    FOOD,

    /** Belleza y estética. */
    BEAUTY,

    /** Salud y bienestar. */
    HEALTH,

    /** Educación y formación. */
    EDUCATION,

    /** Entretenimiento y ocio. */
    ENTERTAINMENT,

    /** Deportes y actividad física. */
    SPORTS,

    /** Servicios generales. */
    SERVICES,

    /** Tecnología y electrónica. */
    TECHNOLOGY,

    /** Categoría por defecto cuando ninguna otra aplica. */
    OTHER,
}
