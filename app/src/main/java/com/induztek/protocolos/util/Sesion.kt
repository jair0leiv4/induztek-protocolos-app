package com.induztek.protocolos.util

import com.induztek.protocolos.model.Rol
import com.induztek.protocolos.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object Sesion {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    fun iniciar(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrar() {
        _usuario.value = null
    }

    fun rolActual(): Rol? = _usuario.value?.obtenerRol()

    fun esAdmin(): Boolean = rolActual() == Rol.ADMIN
}