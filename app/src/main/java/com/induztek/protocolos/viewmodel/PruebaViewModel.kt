package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PruebaUiState(
    val codigoEquipo: String = "",
    val tension: String = "",
    val aislacion: String = "",
    val fechaHora: String = "",
    val codigoEquipoError: String? = null,
    val tensionError: String? = null,
    val aislacionError: String? = null,
    val fechaHoraError: String? = null
)

class PruebaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PruebaUiState(fechaHora = ahora()))
    val uiState: StateFlow<PruebaUiState> = _uiState.asStateFlow()

    private fun ahora(): String =
        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

    fun onCodigoEquipoChange(value: String) {
        _uiState.update { it.copy(codigoEquipo = value, codigoEquipoError = null) }
    }

    fun onTensionChange(value: String) {
        _uiState.update { it.copy(tension = value, tensionError = null) }
    }

    fun onAislacionChange(value: String) {
        _uiState.update { it.copy(aislacion = value, aislacionError = null) }
    }

    fun onFechaHoraChange(value: String) {
        _uiState.update { it.copy(fechaHora = value, fechaHoraError = null) }
    }

    fun onGuardarClick(onSuccess: () -> Unit) {
        val s = _uiState.value
        val codigoError = Validators.validateCodigo(s.codigoEquipo)
        val tensionError = Validators.validateTension(s.tension)
        val aislacionError = Validators.validateAislacion(s.aislacion)
        val fechaError = Validators.validateFechaHora(s.fechaHora)
        _uiState.update {
            it.copy(
                codigoEquipoError = codigoError,
                tensionError = tensionError,
                aislacionError = aislacionError,
                fechaHoraError = fechaError
            )
        }
        if (codigoError == null && tensionError == null && aislacionError == null && fechaError == null) {
            onSuccess()
        }
    }
}