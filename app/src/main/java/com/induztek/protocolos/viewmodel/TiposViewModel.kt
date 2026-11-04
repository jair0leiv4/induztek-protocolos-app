package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TiposState(
    val dialogoAbierto: Boolean = false,
    val editando: TipoEquipo? = null,
    val nombre: String = "",
    val error: String? = null
)

// CRUD de tipos de equipo (catálogo que administra el administrador)
class TiposViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    val tipos: StateFlow<List<TipoEquipo>> = repository.tiposEquipo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _estado = MutableStateFlow(TiposState())
    val estado: StateFlow<TiposState> = _estado.asStateFlow()

    fun abrirNuevo() {
        _estado.value = TiposState(dialogoAbierto = true)
    }

    fun abrirEditar(tipo: TipoEquipo) {
        _estado.value = TiposState(dialogoAbierto = true, editando = tipo, nombre = tipo.nombre)
    }

    fun cerrar() {
        _estado.value = TiposState()
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, error = null) }
    }

    fun guardar() {
        val s = _estado.value
        val error = Validators.validateNombre(s.nombre)
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            val nombre = s.nombre.trim()
            try {
                val ok = if (s.editando != null) {
                    repository.actualizarTipo(s.editando.copy(nombre = nombre))
                } else {
                    repository.guardarTipo(TipoEquipo(nombre = nombre))
                }
                if (ok) cerrar()
                else _estado.update { it.copy(error = "Ya existe un tipo con ese nombre") }
            } catch (e: Exception) {
                _estado.update { it.copy(error = "Ya existe un tipo con ese nombre") }
            }
        }
    }

    fun eliminar(tipo: TipoEquipo, rol: String?) {
        if (!Roles.puedeEditarYEliminar(rol)) return
        viewModelScope.launch { repository.eliminarTipo(tipo) }
    }
}
