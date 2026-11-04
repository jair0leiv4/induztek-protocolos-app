package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.util.Fechas
import com.induztek.protocolos.util.Formato
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PruebaFormState(
    val esEdicion: Boolean = false,
    val idPrueba: Long = 0,
    val codigoEquipo: String = "",
    val tension: String = "",
    val aislacion: String = "",
    val fechaHora: String = "",
    val fotoPath: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val registradaPor: String = "",
    val codigoEquipoError: String? = null,
    val tensionError: String? = null,
    val aislacionError: String? = null,
    val fechaHoraError: String? = null
)

// Formulario de prueba en terreno: mediciones + foto de la placa (CameraX) + ubicación (GPS)
class PruebaFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    private val _estado = MutableStateFlow(
        PruebaFormState(fechaHora = Fechas.formatear(System.currentTimeMillis()))
    )
    val estado: StateFlow<PruebaFormState> = _estado.asStateFlow()

    // Equipos disponibles para elegir (así nunca se registra una prueba de un equipo que no existe)
    val equipos: StateFlow<List<String>> = repository.equipos()
        .map { lista -> lista.map { it.codigo } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Si llega un id válido, es una edición: carga la prueba existente
    fun cargar(id: Long) {
        if (id <= 0) return
        viewModelScope.launch {
            val p = repository.buscarPrueba(id) ?: return@launch
            _estado.update {
                it.copy(
                    esEdicion = true,
                    idPrueba = p.id,
                    codigoEquipo = p.codigoEquipo,
                    tension = Formato.numero(p.tension),
                    aislacion = Formato.numero(p.aislacion),
                    fechaHora = Fechas.formatear(p.fechaMillis),
                    fotoPath = p.fotoPath,
                    latitud = p.latitud,
                    longitud = p.longitud,
                    registradaPor = p.registradaPor
                )
            }
        }
    }

    fun onCodigoEquipoChange(valor: String) {
        _estado.update { it.copy(codigoEquipo = valor, codigoEquipoError = null) }
    }

    fun onTensionChange(valor: String) {
        _estado.update { it.copy(tension = valor, tensionError = null) }
    }

    fun onAislacionChange(valor: String) {
        _estado.update { it.copy(aislacion = valor, aislacionError = null) }
    }

    fun onFechaHoraChange(valor: String) {
        _estado.update { it.copy(fechaHora = valor, fechaHoraError = null) }
    }

    fun onFotoCapturada(ruta: String) {
        _estado.update { it.copy(fotoPath = ruta) }
    }

    fun onUbicacionCapturada(latitud: Double, longitud: Double) {
        _estado.update { it.copy(latitud = latitud, longitud = longitud) }
    }

    fun onGuardarClick(emailUsuario: String, onSuccess: () -> Unit) {
        val s = _estado.value
        val equipoError = Validators.validateEquipoSeleccion(s.codigoEquipo)
        val tensionError = Validators.validateTension(s.tension)
        val aislacionError = Validators.validateAislacion(s.aislacion)
        val fechaError = Validators.validateFechaHora(s.fechaHora)
        _estado.update {
            it.copy(
                codigoEquipoError = equipoError,
                tensionError = tensionError,
                aislacionError = aislacionError,
                fechaHoraError = fechaError
            )
        }
        if (equipoError != null || tensionError != null || aislacionError != null || fechaError != null) return

        viewModelScope.launch {
            val codigo = s.codigoEquipo.trim().uppercase()
            if (!repository.existeEquipo(codigo)) {
                _estado.update { it.copy(codigoEquipoError = "Ese equipo no está registrado") }
                return@launch
            }
            val tension = s.tension.trim().replace(',', '.').toDoubleOrNull() ?: return@launch
            val aislacion = s.aislacion.trim().replace(',', '.').toDoubleOrNull() ?: return@launch
            val fechaMillis = Fechas.aMillis(s.fechaHora) ?: return@launch

            val prueba = Prueba(
                id = if (s.esEdicion) s.idPrueba else 0,
                codigoEquipo = codigo,
                tension = tension,
                aislacion = aislacion,
                fechaMillis = fechaMillis,
                fotoPath = s.fotoPath,
                latitud = s.latitud,
                longitud = s.longitud,
                registradaPor = if (s.esEdicion) s.registradaPor else emailUsuario
            )
            if (s.esEdicion) repository.actualizarPrueba(prueba) else repository.guardarPrueba(prueba)
            onSuccess()
        }
    }
}
