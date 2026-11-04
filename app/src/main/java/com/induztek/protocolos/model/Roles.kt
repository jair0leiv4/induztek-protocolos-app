package com.induztek.protocolos.model

// Los dos tipos de usuario que pueden entrar por el login
object Roles {
    const val ADMIN = "ADMIN"
    const val TECNICO = "TECNICO"

    fun etiqueta(rol: String): String =
        if (rol == ADMIN) "Administrador" else "Técnico"

    fun desdeEtiqueta(etiqueta: String): String =
        if (etiqueta == "Administrador") ADMIN else TECNICO

    val etiquetas = listOf("Administrador", "Técnico")

    fun esAdmin(rol: String?): Boolean = rol == ADMIN

    // Regla de negocio: solo el administrador edita y elimina. El técnico registra y consulta.
    fun puedeEditarYEliminar(rol: String?): Boolean = rol == ADMIN
}
