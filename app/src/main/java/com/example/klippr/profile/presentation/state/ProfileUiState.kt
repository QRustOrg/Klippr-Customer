package com.example.klippr.profile.presentation.state

import com.example.klippr.profile.domain.model.UserProfile
import com.example.klippr.redemption.domain.model.RedemptionCode

// @author Samuel Bonifacio
/** Estado del centro de cuenta. */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val stats: ProfileStats = ProfileStats(),
    val latestRedemptions: List<RedemptionCode> = emptyList(),
    val error: String? = null,
    val activityError: String? = null,
    val profileSaveMessage: String? = null,
)

data class ProfileStats(
    val totalRedemptions: Int = 0,
    val activeRedemptions: Int = 0,
    val redeemedRedemptions: Int = 0,
    val totalSavings: Double? = null,
    val averageTransactionValue: Double? = null,
)
