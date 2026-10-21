package com.induztek.protocolos.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object Validators {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "El correo es obligatorio"
        !emailRegex.matches(email.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isBlank() -> "La contraseña es obligatoria"
        password.length < 6 -> "Debe tener al menos 6 caracteres"
        else -> null
    }
    private val codigoRegex = Regex("^[A-Za-z]{2,4}-\\d{2,6}$")

    fun validateCodigo(codigo: String): String? = when {
        codigo.isBlank() -> "El código es obligatorio"
        !codigoRegex.matches(codigo.trim()) -> "Formato inválido, por ejemplo TR-1042"
        else -> null
    }

    fun validateTipo(tipo: String): String? =
        if (tipo.isBlank()) "Selecciona un tipo de equipo" else null

    fun validateInstalacion(instalacion: String): String? = when {
        instalacion.isBlank() -> "La instalación es obligatoria"
        instalacion.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }
    private fun parseNumero(texto: String): Double? =
        texto.trim().replace(',', '.').toDoubleOrNull()

    fun validateTension(valor: String): String? {
        if (valor.isBlank()) return "La tensión es obligatoria"
        val n = parseNumero(valor) ?: return "Ingresa solo números, por ejemplo 220"
        return if (n <= 0) "Debe ser mayor que 0" else null
    }

    fun validateAislacion(valor: String): String? {
        if (valor.isBlank()) return "La aislación es obligatoria"
        val n = parseNumero(valor) ?: return "Ingresa solo números, por ejemplo 1250"
        return if (n <= 0) "Debe ser mayor que 0" else null
    }

    private val fechaHoraRegex = Regex("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}$")

    fun validateFechaHora(valor: String): String? {
        if (valor.isBlank()) return "La fecha y hora son obligatorias"
        if (!fechaHoraRegex.matches(valor.trim())) return "Formato inválido, por ejemplo 15-10-2026 10:42"
        return try {
            val formato = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            formato.isLenient = false
            formato.parse(valor.trim())
            null
        } catch (e: ParseException) {
            "La fecha u hora no existe"
        }
    }
}