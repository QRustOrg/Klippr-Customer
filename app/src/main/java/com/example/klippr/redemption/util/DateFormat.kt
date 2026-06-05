package com.example.klippr.redemption.util

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.util.Locale

// @author Samuel Bonifacio
private val SPANISH: Locale = Locale.forLanguageTag("es")

private val venceFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMMM yyyy", SPANISH).withZone(ZoneId.systemDefault())

/** Formatea la fecha de vencimiento como "20 Mayo 2026" (mes capitalizado en español). */
fun formatVence(instant: Instant?): String {
    if (instant == null) return "Sin vencimiento"
    val raw = venceFormatter.format(instant) // ej. "20 mayo 2026"
    return raw.split(" ").joinToString(" ") { part ->
        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(SPANISH) else it.toString() }
    }
}
