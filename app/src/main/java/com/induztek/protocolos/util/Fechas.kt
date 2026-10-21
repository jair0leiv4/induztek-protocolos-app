package com.induztek.protocolos.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Fechas {
    private const val PATRON = "dd-MM-yyyy HH:mm"

    fun aMillis(texto: String): Long? = try {
        SimpleDateFormat(PATRON, Locale.getDefault())
            .apply { isLenient = false }
            .parse(texto.trim())?.time
    } catch (e: ParseException) {
        null
    }

    fun formatear(millis: Long): String =
        SimpleDateFormat(PATRON, Locale.getDefault()).format(Date(millis))
}