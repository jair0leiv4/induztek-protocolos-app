package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.util.Sesion
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

    private val repositorio = ProtocolosRepository.crear(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, loginError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, loginError = null) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val actual = _uiState.value
        if (actual.cargando) return

        val emailError = Validators.validateEmail(actual.email)
        val passwordError = Validators.validatePassword(actual.password)
        _uiState.update {
            it.copy(emailError = emailError, passwordError = passwordError, loginError = null)
        }
        if (emailError != null || passwordError != null) return

        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            repositorio.asegurarUsuariosIniciales()
            val usuario = repositorio.autenticar(actual.email, actual.password)
            if (usuario != null) {
                Sesion.iniciar(usuario)
                _uiState.update { it.copy(cargando = false) }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(cargando = false, loginError = "Correo o contraseña incorrectos")
                }
            }
        }
    }
}