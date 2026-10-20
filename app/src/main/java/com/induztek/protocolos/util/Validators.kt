package com.induztek.protocolos.util

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
}