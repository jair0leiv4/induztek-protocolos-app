package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.ConfirmarDialogo
import com.induztek.protocolos.ui.components.PantallaConMenu
import com.induztek.protocolos.viewmodel.EquiposViewModel
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel

// Lista de equipos (READ). El administrador además edita y elimina (UPDATE y DELETE).
@Composable
fun EquiposScreen(
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    equiposViewModel: EquiposViewModel = viewModel()
) {
    val equipos by equiposViewModel.equipos.collectAsState()
    val busqueda by equiposViewModel.busqueda.collectAsState()
    val sesionUi by sesionViewModel.estado.collectAsState()
    val rol = sesionUi.sesion?.rol
    val puedeGestionar = Roles.puedeEditarYEliminar(rol)
    var equipoAEliminar by remember { mutableStateOf<Equipo?>(null) }

    PantallaConMenu(
        titulo = "Equipos",
        rutaActual = Screen.Equipos.route,
        mainViewModel = mainViewModel,
        sesionViewModel = sesionViewModel,
        botonFlotante = {
            ExtendedFloatingActionButton(onClick = { mainViewModel.navigateTo(Screen.equipoForm()) }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar equipo")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = busqueda,
                onValueChange = equiposViewModel::onBusquedaChange,
                label = { Text("Buscar por código, tipo o instalación") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${equipos.size} equipos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (equipos.isEmpty()) {
                Text(
                    text = "No hay equipos para mostrar. Registra el primero con el botón de abajo.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(equipos, key = { it.codigo }) { equipo ->
                        Card(modifier = Modifier.fillMaxWidth().animateItem()) {
                            Row(
                                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(equipo.codigo, style = MaterialTheme.typography.titleMedium)
                                    Text(equipo.tipo, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        equipo.instalacion,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (puedeGestionar) {
                                    IconButton(onClick = {
                                        mainViewModel.navigateTo(Screen.equipoForm(equipo.codigo))
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar equipo")
                                    }
                                    IconButton(onClick = { equipoAEliminar = equipo }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar equipo",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    equipoAEliminar?.let { equipo ->
        ConfirmarDialogo(
            titulo = "Eliminar equipo",
            mensaje = "Se eliminará ${equipo.codigo} junto con todas sus pruebas. ¿Continuar?",
            onConfirmar = {
                equiposViewModel.eliminar(equipo, rol)
                equipoAEliminar = null
            },
            onCancelar = { equipoAEliminar = null }
        )
    }
}
