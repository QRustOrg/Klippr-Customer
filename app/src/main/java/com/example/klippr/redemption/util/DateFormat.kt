package com.example.klippr.redemption.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.util.Locale

// @author Samuel Bonifacio
private val SPANISH: Locale = Locale.forLanguageTag("es")

private val venceFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMMM yyyy", SPANISH).withZone(ZoneId.systemDefault())

private val horaFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("h:mm a", SPANISH).withZone(ZoneId.systemDefault())

/** Formatea la fecha de vencimiento como "20 Mayo 2026" (mes capitalizado en español). */
fun formatVence(instant: Instant?): String {
    if (instant == null) return "Sin vencimiento"
    val raw = venceFormatter.format(instant) // ej. "20 mayo 2026"
    return raw.split(" ").joinToString(" ") { part ->
        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(SPANISH) else it.toString() }
    }
}

/** Formatea la hora del canje como "10:30 AM". */
fun formatHora(instant: Instant?): String {
    if (instant == null) return ""
    return horaFormatter.format(instant).uppercase(SPANISH) // "10:30 a. m." → "10:30 A. M." → normaliza abajo
        .replace("A. M.", "AM").replace("P. M.", "PM").replace("A.M.", "AM").replace("P.M.", "PM")
}

/** Agrupa por día relativo: "Hoy" / "Ayer" / fecha formateada (para el Historial). */
fun dayBucket(instant: Instant?): String {
    if (instant == null) return "Sin fecha"
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now(ZoneId.systemDefault())
    return when (date) {
        today -> "Hoy"
        today.minusDays(1) -> "Ayer"
        else -> formatVence(instant)
    }
}
