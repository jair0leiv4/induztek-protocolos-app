package com.induztek.protocolos.model

object Roles {
    const val ADMIN = "ADMIN"
    const val TECNICO = "TECNICO"

    // Texto para mostrar en pantalla
    fun etiqueta(rol: String): String =
        if (rol == ADMIN) "Administrador" else "Técnico"
}