package com.example.klippr.profile.domain.model

import com.google.gson.annotations.SerializedName

// @author Samuel Bonifacio
/** Preferencias de cuenta sincronizadas con `/api/v1/Preferences`. */
data class UserPreference(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("userId") val userId: String,
    @SerializedName("darkMode") val darkMode: Boolean,
    @SerializedName("languageCode") val languageCode: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("emailNotifications") val emailNotifications: Boolean,
    @SerializedName("pushNotifications") val pushNotifications: Boolean,
    @SerializedName("smsNotifications") val smsNotifications: Boolean,
    @SerializedName("profileVisibility") val profileVisibility: String,
    @SerializedName("dataSharingConsent") val dataSharingConsent: Boolean,
) {
    companion object {
        fun defaults(userId: String) = UserPreference(
            userId = userId,
            darkMode = false,
            languageCode = "es",
            timezone = "America/Bogota",
            emailNotifications = true,
            pushNotifications = true,
            smsNotifications = false,
            profileVisibility = "private",
            dataSharingConsent = false,
        )
    }
}

data class UserPreferenceRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("darkMode") val darkMode: Boolean,
    @SerializedName("languageCode") val languageCode: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("emailNotifications") val emailNotifications: Boolean,
    @SerializedName("pushNotifications") val pushNotifications: Boolean,
    @SerializedName("smsNotifications") val smsNotifications: Boolean,
    @SerializedName("profileVisibility") val profileVisibility: String,
    @SerializedName("dataSharingConsent") val dataSharingConsent: Boolean,
)

fun UserPreference.toRequest() = UserPreferenceRequest(
    userId = userId,
    darkMode = darkMode,
    languageCode = languageCode,
    timezone = timezone,
    emailNotifications = emailNotifications,
    pushNotifications = pushNotifications,
    smsNotifications = smsNotifications,
    profileVisibility = profileVisibility,
    dataSharingConsent = dataSharingConsent,
)
