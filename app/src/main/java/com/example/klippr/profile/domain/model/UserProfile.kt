package com.example.klippr.profile.domain.model

// @author Samuel Bonifacio
/** Perfil del usuario autenticado. Modelo de dominio puro para la pantalla de perfil. */
data class UserProfile(
    val userId: String,
    val email: String,
    val role: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean,
    /** Fecha de registro ya formateada para mostrar (o vacío si no llegó). */
    val memberSince: String,
) {
    /** Nombre completo legible; cae a la parte local del email si no hay nombre. */
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
            .ifBlank { email.substringBefore("@") }

    /** Solo el primer nombre, para saludos ("Hola, {firstName}!"). */
    val greetingName: String
        get() = firstName.ifBlank { email.substringBefore("@") }
}
