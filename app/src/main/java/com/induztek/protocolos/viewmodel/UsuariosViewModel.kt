package com.induztek.protocolos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.ProtocolosRepository
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.model.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Gestión de usuarios (solo administrador)
class UsuariosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProtocolosRepository.crear(application)

    val usuarios: StateFlow<List<Usuario>> = repository.usuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reglas: solo el administrador elimina, y nadie puede eliminarse a sí mismo
    fun eliminar(usuario: Usuario, emailActual: String?, rol: String?) {
        if (!Roles.puedeEditarYEliminar(rol)) return
        if (usuario.email == emailActual) return
        viewModelScope.launch { repository.eliminarUsuario(usuario) }
    }
}
