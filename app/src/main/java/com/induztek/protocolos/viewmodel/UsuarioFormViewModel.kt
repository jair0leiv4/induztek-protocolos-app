package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Rol
import com.induztek.protocolos.util.EdicionUsuario
import com.induztek.protocolos.util.Sesion
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuarioFormUiState(
    val esEdicion: Boolean = false,
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val rol: Rol? = null,
    val nombreError: String? = null,
    val correoError: String? = null,
    val contrasenaError: String? = null,
    val rolError: String? = null,
    val errorGeneral: String? = null,
    val guardando: Boolean = false
)

class UsuarioFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProtocolosRepository.crear(application)

    private val _uiState = MutableStateFlow(UsuarioFormUiState())
    val uiState: StateFlow<UsuarioFormUiState> = _uiState.asStateFlow()

    init {
        val correoEdicion = EdicionUsuario.correo
        if (correoEdicion != null) {
            viewModelScope.launch {
                val usuario = repositorio.buscarUsuario(correoEdicion)
                if (usuario != null) {
                    _uiState.update {
                        it.copy(
                            esEdicion = true,
                            correo = usuario.correo,
                            nombre = usuario.nombre,
                            rol = usuario.obtenerRol()
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, nombreError = null, errorGeneral = null) }
    }

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, correoError = null, errorGeneral = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, contrasenaError = null, errorGeneral = null) }
    }

    fun onRolChange(valor: Rol) {
        _uiState.update { it.copy(rol = valor, rolError = null, errorGeneral = null) }
    }

    fun guardar(onExito: () -> Unit) {
        val actual = _uiState.value
        if (actual.guardando) return

        val nombreError = Validators.validateNombre(actual.nombre)
        val correoError = if (actual.esEdicion) null else Validators.validateEmail(actual.correo)
        val contrasenaError =
            if (actual.esEdicion && actual.contrasena.isBlank()) null
            else Validators.validatePassword(actual.contrasena)
        val rolError = Validators.validateRolUsuario(actual.rol?.name ?: "")

        _uiState.update {
            it.copy(
                nombreError = nombreError,
                correoError = correoError,
                contrasenaError = contrasenaError,
                rolError = rolError,
                errorGeneral = null
            )
        }
        if (nombreError != null || correoError != null || contrasenaError != null || rolError != null) return
        val rol = actual.rol ?: return

        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            if (actual.esEdicion) {
                val usuarios = repositorio.usuarios().first()
                val existente = usuarios.firstOrNull { it.correo == actual.correo }
                val cantidadAdmins = usuarios.count { it.obtenerRol() == Rol.ADMIN }
                if (existente?.obtenerRol() == Rol.ADMIN && rol != Rol.ADMIN && cantidadAdmins <= 1) {
                    _uiState.update {
                        it.copy(guardando = false, errorGeneral = "Debe quedar al menos un administrador")
                    }
                    return@launch
                }

                val ok = repositorio.actualizarUsuario(
                    correo = actual.correo,
                    nombre = actual.nombre,
                    rol = rol,
                    nuevaContrasena = actual.contrasena
                )
                if (ok) {
                    if (Sesion.usuario.value?.correo == actual.correo) {
                        repositorio.buscarUsuario(actual.correo)?.let { Sesion.iniciar(it) }
                    }
                    _uiState.update { it.copy(guardando = false) }
                    onExito()
                } else {
                    _uiState.update {
                        it.copy(guardando = false, errorGeneral = "No se pudo actualizar el usuario")
                    }
                }
            } else {
                if (repositorio.existeUsuario(actual.correo)) {
                    _uiState.update {
                        it.copy(guardando = false, correoError = "Ya existe un usuario con ese correo")
                    }
                    return@launch
                }

                val ok = repositorio.crearUsuario(
                    nombre = actual.nombre,
                    correo = actual.correo,
                    contrasena = actual.contrasena,
                    rol = rol
                )
                if (ok) {
                    _uiState.update { it.copy(guardando = false) }
                    onExito()
                } else {
                    _uiState.update {
                        it.copy(guardando = false, errorGeneral = "No se pudo crear el usuario")
                    }
                }
            }
        }
    }
}