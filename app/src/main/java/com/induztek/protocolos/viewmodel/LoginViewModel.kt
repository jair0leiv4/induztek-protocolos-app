package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val emailError = Validators.validateEmail(_uiState.value.email)
        val passwordError = Validators.validatePassword(_uiState.value.password)
        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
        if (emailError == null && passwordError == null) onSuccess()
    }
}