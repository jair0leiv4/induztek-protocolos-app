package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.Roles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Una prueba junto con su variación de aislación respecto a la medición anterior del mismo equipo
data class PruebaItem(
    val prueba: Prueba,
    val variacionAislacion: Double?
)

// Historial de pruebas: compara cada medición con la anterior (mejora esperada del caso)
class PruebasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    val items: StateFlow<List<PruebaItem>> =
        combine(repository.pruebas(), _filtro) { lista, texto ->
            // variación (%) = (actual - anterior) / anterior * 100, calculada por equipo y en orden de fecha
            val variaciones = mutableMapOf<Long, Double?>()
            lista.groupBy { it.codigoEquipo }.forEach { (_, delEquipo) ->
                val ordenadas = delEquipo.sortedBy { it.fechaMillis }
                ordenadas.forEachIndexed { i, prueba ->
                    val anterior = ordenadas.getOrNull(i - 1)
                    variaciones[prueba.id] =
                        if (anterior != null && anterior.aislacion != 0.0) {
                            (prueba.aislacion - anterior.aislacion) / anterior.aislacion * 100.0
                        } else {
                            null
                        }
                }
            }
            val t = texto.trim()
            lista
                .filter { t.isEmpty() || it.codigoEquipo.contains(t, ignoreCase = true) }
                .map { PruebaItem(it, variaciones[it.id]) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onFiltroChange(valor: String) {
        _filtro.value = valor
    }

    fun eliminar(prueba: Prueba, rol: String?) {
        if (!Roles.puedeEditarYEliminar(rol)) return
        viewModelScope.launch { repository.eliminarPrueba(prueba) }
    }
}
