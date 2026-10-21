package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoUiState(
    val codigo: String = "",
    val tipo: String = "",
    val instalacion: String = "",
    val codigoError: String? = null,
    val tipoError: String? = null,
    val instalacionError: String? = null
)

class EquipoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

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
        if (codigoError == null && tipoError == null && instalacionError == null) {
            viewModelScope.launch {
                val guardado = repository.guardarEquipo(
                    Equipo(
                        codigo = s.codigo.trim().uppercase(),
                        tipo = s.tipo,
                        instalacion = s.instalacion.trim()
                    )
                )
                if (guardado) {
                    onSuccess()
                } else {
                    _uiState.update { it.copy(codigoError = "Ya existe un equipo con ese código") }
                }
            }
        }
    }
}