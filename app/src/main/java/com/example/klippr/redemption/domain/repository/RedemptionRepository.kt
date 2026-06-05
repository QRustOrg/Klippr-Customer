package com.example.klippr.redemption.domain.repository

import com.example.klippr.promotions.domain.model.Promotion
import com.example.klippr.redemption.domain.model.RedemptionCode

// @author Samuel Bonifacio
/** Contrato del BC Redemption: genera y consulta códigos de canje (US-04/05/06). */
interface RedemptionRepository {

    /** US-04: genera un código de canje único para [promotion] del consumidor [consumerId]. */
    suspend fun generate(consumerId: String, promotion: Promotion): RedemptionCode

    /** US-05/06: historial completo de códigos del consumidor (las 3 pestañas). */
    suspend fun getByConsumer(consumerId: String): List<RedemptionCode>

    suspend fun getById(id: String): RedemptionCode
}
