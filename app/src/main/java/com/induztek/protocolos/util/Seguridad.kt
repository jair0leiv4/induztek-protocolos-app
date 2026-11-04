package com.induztek.protocolos.util

import java.security.MessageDigest

object Seguridad {

    // Convierte una contraseña en una "huella" de 64 caracteres que no se puede revertir
    fun sha256(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
