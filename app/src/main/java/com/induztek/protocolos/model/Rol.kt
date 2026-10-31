package com.induztek.protocolos.model

enum class Rol(val etiqueta: String) {
    ADMIN("Administrador"),
    TECNICO("Técnico");

    companion object {
        fun desde(valor: String): Rol =
            values().firstOrNull { it.name == valor } ?: TECNICO
    }
}