package com.example.klippr.redemption.application.usecase

import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.data.store.RedemptionStore

// @author Samuel Bonifacio
/** Obtiene un codigo de canje por id para abrir el QR aunque no este cargado en memoria. */
class GetRedemptionByIdUseCase(private val repository: RedemptionStore) {
    suspend operator fun invoke(id: String): RedemptionCode = repository.getById(id)
}
