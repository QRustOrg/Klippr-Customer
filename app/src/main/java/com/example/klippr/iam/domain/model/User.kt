package com.example.klippr.iam.domain.model

// @author Samuel Bonifacio
/** Identidad del usuario autenticado. Entidad de dominio pura. */
data class User(
    val userId: String,
    val email: String,
    val role: String,
)

/** Sesión persistida: identidad + token de acceso para llamadas autenticadas. */
data class Session(
    val token: String,
    val user: User,
)
