package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Roles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Lista de equipos con búsqueda. Solo el administrador puede eliminar.
class EquiposViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val equipos: StateFlow<List<Equipo>> =
        combine(repository.equipos(), _busqueda) { lista, texto ->
            val t = texto.trim()
            if (t.isEmpty()) {
                lista
            } else {
                lista.filter {
                    it.codigo.contains(t, ignoreCase = true) ||
                        it.tipo.contains(t, ignoreCase = true) ||
                        it.instalacion.contains(t, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBusquedaChange(valor: String) {
        _busqueda.value = valor
    }

    fun eliminar(equipo: Equipo, rol: String?) {
        if (!Roles.puedeEditarYEliminar(rol)) return
        viewModelScope.launch { repository.eliminarEquipo(equipo) }
    }
}
