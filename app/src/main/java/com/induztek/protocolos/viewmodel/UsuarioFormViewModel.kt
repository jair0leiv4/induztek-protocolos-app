package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.util.Seguridad
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuarioFormState(
    val esEdicion: Boolean = false,
    val id: Long = 0,
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val passwordActual: String = "",
    val rol: String = Roles.TECNICO,
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val rolError: String? = null
)

// Formulario para crear o editar un usuario (administrador o técnico)
class UsuarioFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    private val _estado = MutableStateFlow(UsuarioFormState())
    val estado: StateFlow<UsuarioFormState> = _estado.asStateFlow()

    fun cargar(id: Long) {
        if (id <= 0) return
        viewModelScope.launch {
            val u = repository.buscarUsuario(id) ?: return@launch
            _estado.update {
                it.copy(
                    esEdicion = true,
                    id = u.id,
                    nombre = u.nombre,
                    email = u.email,
                    passwordActual = u.password,
                    rol = u.rol
                )
            }
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onEmailChange(valor: String) {
        _estado.update { it.copy(email = valor, emailError = null) }
    }

    fun onPasswordChange(valor: String) {
        _estado.update { it.copy(password = valor, passwordError = null) }
    }

    fun onRolChange(etiqueta: String) {
        _estado.update { it.copy(rol = Roles.desdeEtiqueta(etiqueta), rolError = null) }
    }

    fun onGuardarClick(onSuccess: () -> Unit) {
        val s = _estado.value
        val nombreError = Validators.validateNombre(s.nombre)
        val emailError = Validators.validateEmail(s.email)
        // Al editar, dejar la contraseña vacía significa "mantener la actual"
        val passwordError =
            if (s.esEdicion && s.password.isBlank()) null else Validators.validatePassword(s.password)
        val rolError = Validators.validateRol(s.rol)
        _estado.update {
            it.copy(
                nombreError = nombreError,
                emailError = emailError,
                passwordError = passwordError,
                rolError = rolError
            )
        }
        if (nombreError != null || emailError != null || passwordError != null || rolError != null) return

        viewModelScope.launch {
            val hash =
                if (s.esEdicion && s.password.isBlank()) s.passwordActual else Seguridad.sha256(s.password)
            val usuario = Usuario(
                id = if (s.esEdicion) s.id else 0,
                nombre = s.nombre.trim(),
                email = s.email.trim().lowercase(),
                password = hash,
                rol = s.rol
            )
            try {
                val ok =
                    if (s.esEdicion) repository.actualizarUsuario(usuario) else repository.guardarUsuario(usuario)
                if (ok) onSuccess()
                else _estado.update { it.copy(emailError = "Ya existe un usuario con ese correo") }
            } catch (e: Exception) {
                // El correo es único: si ya existe, Room lanza una excepción al actualizar
                _estado.update { it.copy(emailError = "Ya existe un usuario con ese correo") }
            }
        }
    }
}
