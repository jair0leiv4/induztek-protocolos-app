package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.util.Fechas
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PruebaUiState(
    val codigoEquipo: String = "",
    val tension: String = "",
    val aislacion: String = "",
    val fechaHora: String = "",
    val fotoPath: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val codigoEquipoError: String? = null,
    val tensionError: String? = null,
    val aislacionError: String? = null,
    val fechaHoraError: String? = null
)

class PruebaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    private val _uiState = MutableStateFlow(PruebaUiState(fechaHora = Fechas.formatear(System.currentTimeMillis())))
    val uiState: StateFlow<PruebaUiState> = _uiState.asStateFlow()

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

    fun onFotoCapturada(ruta: String) {
        _uiState.update { it.copy(fotoPath = ruta) }
    }

    fun onUbicacionCapturada(latitud: Double, longitud: Double) {
        _uiState.update { it.copy(latitud = latitud, longitud = longitud) }
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
        if (codigoError != null || tensionError != null || aislacionError != null || fechaError != null) return

        viewModelScope.launch {
            val codigo = s.codigoEquipo.trim().uppercase()
            if (!repository.existeEquipo(codigo)) {
                _uiState.update { it.copy(codigoEquipoError = "Ese equipo no está registrado") }
                return@launch
            }
            val tension = s.tension.trim().replace(',', '.').toDoubleOrNull() ?: return@launch
            val aislacion = s.aislacion.trim().replace(',', '.').toDoubleOrNull() ?: return@launch
            val fechaMillis = Fechas.aMillis(s.fechaHora) ?: return@launch

            repository.guardarPrueba(
                Prueba(
                    codigoEquipo = codigo,
                    tension = tension,
                    aislacion = aislacion,
                    fechaMillis = fechaMillis,
                    fotoPath = s.fotoPath,
                    latitud = s.latitud,
                    longitud = s.longitud
                )
            )
            onSuccess()
        }
    }
}