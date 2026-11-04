package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoFormState(
    val esEdicion: Boolean = false,
    val codigo: String = "",
    val tipo: String = "",
    val instalacion: String = "",
    val codigoError: String? = null,
    val tipoError: String? = null,
    val instalacionError: String? = null
)

// Formulario para crear o editar un equipo
class EquipoFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    private val _estado = MutableStateFlow(EquipoFormState())
    val estado: StateFlow<EquipoFormState> = _estado.asStateFlow()

    // Los tipos disponibles vienen de la base de datos (los administra el administrador)
    val tipos: StateFlow<List<String>> = repository.tiposEquipo()
        .map { lista -> lista.map { it.nombre } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Si llega un código, es una edición: carga el equipo existente
    fun cargar(codigo: String?) {
        if (codigo == null) return
        viewModelScope.launch {
            val equipo = repository.buscarEquipo(codigo) ?: return@launch
            _estado.update {
                it.copy(
                    esEdicion = true,
                    codigo = equipo.codigo,
                    tipo = equipo.tipo,
                    instalacion = equipo.instalacion
                )
            }
        }
    }

    fun onCodigoChange(valor: String) {
        _estado.update { it.copy(codigo = valor, codigoError = null) }
    }

    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor, tipoError = null) }
    }

    fun onInstalacionChange(valor: String) {
        _estado.update { it.copy(instalacion = valor, instalacionError = null) }
    }

    fun onGuardarClick(onSuccess: () -> Unit) {
        val s = _estado.value
        // En edición el código no se puede cambiar (es la clave del equipo)
        val codigoError = if (s.esEdicion) null else Validators.validateCodigo(s.codigo)
        val tipoError = Validators.validateTipo(s.tipo)
        val instalacionError = Validators.validateInstalacion(s.instalacion)
        _estado.update {
            it.copy(codigoError = codigoError, tipoError = tipoError, instalacionError = instalacionError)
        }
        if (codigoError != null || tipoError != null || instalacionError != null) return

        viewModelScope.launch {
            val equipo = Equipo(
                codigo = s.codigo.trim().uppercase(),
                tipo = s.tipo,
                instalacion = s.instalacion.trim()
            )
            if (s.esEdicion) {
                val ok = repository.actualizarEquipo(equipo)
                if (ok) onSuccess()
                else _estado.update { it.copy(codigoError = "No se pudo actualizar el equipo") }
            } else {
                val ok = repository.guardarEquipo(equipo)
                if (ok) onSuccess()
                else _estado.update { it.copy(codigoError = "Ya existe un equipo con ese código") }
            }
        }
    }
}
