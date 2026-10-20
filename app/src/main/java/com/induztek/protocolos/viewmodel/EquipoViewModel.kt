package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EquipoUiState(
    val codigo: String = "",
    val tipo: String = "",
    val instalacion: String = "",
    val codigoError: String? = null,
    val tipoError: String? = null,
    val instalacionError: String? = null
)

class EquipoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EquipoUiState())
    val uiState: StateFlow<EquipoUiState> = _uiState.asStateFlow()

    val tiposDisponibles = listOf(
        "Transformador",
        "Relé de protección",
        "Celda de media tensión",
        "Cable"
    )

    fun onCodigoChange(value: String) {
        _uiState.update { it.copy(codigo = value, codigoError = null) }
    }

    fun onTipoChange(value: String) {
        _uiState.update { it.copy(tipo = value, tipoError = null) }
    }

    fun onInstalacionChange(value: String) {
        _uiState.update { it.copy(instalacion = value, instalacionError = null) }
    }

    fun onGuardarClick(onSuccess: () -> Unit) {
        val s = _uiState.value
        val codigoError = Validators.validateCodigo(s.codigo)
        val tipoError = Validators.validateTipo(s.tipo)
        val instalacionError = Validators.validateInstalacion(s.instalacion)
        _uiState.update {
            it.copy(
                codigoError = codigoError,
                tipoError = tipoError,
                instalacionError = instalacionError
            )
        }
        if (codigoError == null && tipoError == null && instalacionError == null) onSuccess()
    }
}