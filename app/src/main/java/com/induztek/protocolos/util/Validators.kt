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
}