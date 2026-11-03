package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Rol
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.util.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioListViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProtocolosRepository.crear(application)

    val usuarios: StateFlow<List<Usuario>> = repositorio.usuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun eliminar(usuario: Usuario) {
        viewModelScope.launch {
            val cantidadAdmins = usuarios.value.count { it.obtenerRol() == Rol.ADMIN }
            when {
                Sesion.usuario.value?.correo == usuario.correo ->
                    _mensaje.value = "No puedes eliminar tu propio usuario"

                usuario.obtenerRol() == Rol.ADMIN && cantidadAdmins <= 1 ->
                    _mensaje.value = "Debe quedar al menos un administrador"

                else -> {
                    val ok = repositorio.eliminarUsuario(usuario.correo)
                    _mensaje.value = if (ok) "Usuario eliminado" else "No se pudo eliminar"
                }
            }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}