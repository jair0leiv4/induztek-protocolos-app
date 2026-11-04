package com.induztek.protocolos.navigation

// Todas las "direcciones" de la app. Los formularios reciben un argumento opcional (para editar).
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Equipos : Screen("equipos")
    data object Pruebas : Screen("pruebas")
    data object Usuarios : Screen("usuarios")
    data object Tipos : Screen("tipos")
    data object EquipoForm : Screen("equipo_form?codigo={codigo}")
    data object PruebaForm : Screen("prueba_form?id={id}")
    data object UsuarioForm : Screen("usuario_form?id={id}")

    companion object {
        const val ARG_CODIGO = "codigo"
        const val ARG_ID = "id"

        fun equipoForm(codigo: String? = null): String =
            if (codigo == null) "equipo_form" else "equipo_form?codigo=$codigo"

        fun pruebaForm(id: Long? = null): String =
            if (id == null) "prueba_form" else "prueba_form?id=$id"

        fun usuarioForm(id: Long? = null): String =
            if (id == null) "usuario_form" else "usuario_form?id=$id"
    }
}
