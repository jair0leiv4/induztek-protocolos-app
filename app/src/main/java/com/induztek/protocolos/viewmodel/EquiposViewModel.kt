package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Equipo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EquiposViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    val equipos: StateFlow<List<Equipo>> = repository.equipos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}