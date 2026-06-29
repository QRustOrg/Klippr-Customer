package com.example.klippr.redemption.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.util.Locale

// @author Samuel Bonifacio
private val SPANISH: Locale = Locale.forLanguageTag("es")

private val venceFormatter: java.text.SimpleDateFormat =
    java.text.SimpleDateFormat("d MMMM yyyy", SPANISH)

private val horaFormatter: java.text.SimpleDateFormat =
    java.text.SimpleDateFormat("h:mm a", SPANISH)

/** Formatea la fecha de vencimiento como "20 Mayo 2026" (mes capitalizado en español). */
fun formatVence(instant: Long?): String {
    if (instant == null) return "Sin vencimiento"
    val raw = venceFormatter.format(java.util.Date(instant)) // ej. "20 mayo 2026"
    return raw.split(" ").joinToString(" ") { part ->
        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(SPANISH) else it.toString() }
    }
}

/** Formatea la hora del canje como "10:30 AM". */
fun formatHora(instant: Long?): String {
    if (instant == null) return ""
    return horaFormatter.format(java.util.Date(instant)).uppercase(SPANISH)
        .replace("A. M.", "AM").replace("P. M.", "PM").replace("A.M.", "AM").replace("P.M.", "PM")
}

/** Agrupa por día relativo: "Hoy" / "Ayer" / fecha formateada (para el Historial). */
fun dayBucket(instant: Long?): String {
    if (instant == null) return "Sin fecha"
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = instant }
    val today = java.util.Calendar.getInstance()
    val yesterday = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }
    fun sameDay(a: java.util.Calendar, b: java.util.Calendar) =
        a.get(java.util.Calendar.YEAR) == b.get(java.util.Calendar.YEAR) &&
                a.get(java.util.Calendar.DAY_OF_YEAR) == b.get(java.util.Calendar.DAY_OF_YEAR)
    return when {
        sameDay(cal, today) -> "Hoy"
        sameDay(cal, yesterday) -> "Ayer"
        else -> formatVence(instant)
    }
}
