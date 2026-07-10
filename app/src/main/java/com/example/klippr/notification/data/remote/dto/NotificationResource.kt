package com.example.klippr.notification.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Resource flexible del backend. OpenAPI no documenta el response del GET;
 * se aceptan alias comunes de campos.
 */
data class NotificationResource(
    @SerializedName(value = "id", alternate = ["notificationId", "NotificationId"])
    val id: String? = null,
    @SerializedName(value = "userId", alternate = ["UserId"])
    val userId: String? = null,
    @SerializedName(value = "type", alternate = ["Type"])
    val type: String? = null,
    @SerializedName(value = "title", alternate = ["Title"])
    val title: String? = null,
    @SerializedName(value = "message", alternate = ["Message", "body"])
    val message: String? = null,
    @SerializedName(value = "relatedId", alternate = ["RelatedId"])
    val relatedId: String? = null,
    @SerializedName(value = "isRead", alternate = ["read", "IsRead"])
    val isRead: Boolean? = null,
    @SerializedName(value = "createdAt", alternate = ["createdOn", "CreatedAt"])
    val createdAt: String? = null,
)

data class CreateNotificationBody(
    @SerializedName("userId") val userId: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("relatedId") val relatedId: String? = null,
)
