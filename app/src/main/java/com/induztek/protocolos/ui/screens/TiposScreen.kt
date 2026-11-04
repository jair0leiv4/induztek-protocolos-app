package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.ui.components.ConfirmarDialogo
import com.induztek.protocolos.ui.components.PantallaConMenu
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import com.induztek.protocolos.viewmodel.TiposViewModel

// CRUD de tipos de equipo con un cuadro de diálogo para crear y editar. Solo el administrador.
@Composable
fun TiposScreen(
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    tiposViewModel: TiposViewModel = viewModel()
) {
    val tipos by tiposViewModel.tipos.collectAsState()
    val estado by tiposViewModel.estado.collectAsState()
    val sesionUi by sesionViewModel.estado.collectAsState()
    val rol = sesionUi.sesion?.rol
    val esAdmin = Roles.esAdmin(rol)
    var tipoAEliminar by remember { mutableStateOf<TipoEquipo?>(null) }

    PantallaConMenu(
        titulo = "Tipos de equipo",
        rutaActual = Screen.Tipos.route,
        mainViewModel = mainViewModel,
        sesionViewModel = sesionViewModel,
        botonFlotante = {
            if (esAdmin) {
                ExtendedFloatingActionButton(onClick = { tiposViewModel.abrirNuevo() }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo tipo")
                }
            }
        }
    ) {
        if (!esAdmin) {
            Text(
                text = "Solo el administrador puede gestionar los tipos de equipo.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
            ) {
                items(tipos, key = { it.id }) { tipo ->
                    Card(modifier = Modifier.fillMaxWidth().animateItem()) {
                        Row(
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tipo.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { tiposViewModel.abrirEditar(tipo) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar tipo")
                            }
                            IconButton(onClick = { tipoAEliminar = tipo }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar tipo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (estado.dialogoAbierto) {
        AlertDialog(
            onDismissRequest = tiposViewModel::cerrar,
            title = { Text(if (estado.editando == null) "Nuevo tipo de equipo" else "Editar tipo de equipo") },
            text = {
                CampoValidado(
                    value = estado.nombre,
                    onValueChange = tiposViewModel::onNombreChange,
                    label = "Nombre del tipo",
                    icono = Icons.Default.Build,
                    error = estado.error
                )
            },
            confirmButton = { TextButton(onClick = tiposViewModel::guardar) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = tiposViewModel::cerrar) { Text("Cancelar") } }
        )
    }

    tipoAEliminar?.let { tipo ->
        ConfirmarDialogo(
            titulo = "Eliminar tipo",
            mensaje = "Se eliminará el tipo ${tipo.nombre}. Los equipos que ya lo tienen conservan su nombre. ¿Continuar?",
            onConfirmar = {
                tiposViewModel.eliminar(tipo, rol)
                tipoAEliminar = null
            },
            onCancelar = { tipoAEliminar = null }
        )
    }
}
