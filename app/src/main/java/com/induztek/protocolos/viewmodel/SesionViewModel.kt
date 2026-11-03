package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.Sesion
import com.induztek.protocolos.data.SesionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// cargando = true mientras DataStore todavía no responde
data class SesionUi(
    val cargando: Boolean = true,
    val sesion: Sesion? = null
)

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val manager = SesionManager(application)

    val estado: StateFlow<SesionUi> = manager.sesion
        .map { SesionUi(cargando = false, sesion = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SesionUi())

    fun cerrarSesion() {
        viewModelScope.launch { manager.cerrar() }
    }
}