package com.induztek.protocolos.util

import java.util.Locale
import kotlin.math.abs

object Formato {

    // 220.0 -> "220", 220.5 -> "220.5"
    fun numero(valor: Double): String =
        if (valor % 1.0 == 0.0) {
            valor.toLong().toString()
        } else {
            "%.2f".format(Locale.US, valor).trimEnd('0').trimEnd('.')
        }

    // -4.567 -> "4.6 %"
    fun porcentaje(valor: Double): String =
        "%.1f".format(Locale.US, abs(valor)) + " %"
}
