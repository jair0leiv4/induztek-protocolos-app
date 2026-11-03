package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.data.SesionManager
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val cargando: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)
    private val sesionManager = SesionManager(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, loginError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, loginError = null) }
    }

    fun onLoginClick(onSuccess: (Usuario) -> Unit) {
        val s = _uiState.value

        // 1. Validar el formato de lo que escribió
        val emailError = Validators.validateEmail(s.email)
        val passwordError = Validators.validatePassword(s.password)
        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
        if (emailError != null || passwordError != null) return

        // 2. Comparar con la base de datos (en segundo plano)
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, loginError = null) }

            repository.sembrarDatosIniciales() // crea admin y técnico si la base está vacía
            val usuario = repository.autenticar(s.email, s.password)

            if (usuario == null) {
                _uiState.update {
                    it.copy(cargando = false, loginError = "Correo o contraseña incorrectos")
                }
            } else {
                sesionManager.guardar(usuario)
                _uiState.update { it.copy(cargando = false) }
                onSuccess(usuario)
            }
        }
    }
}