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
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.ConfirmarDialogo
import com.induztek.protocolos.ui.components.PantallaConMenu
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import com.induztek.protocolos.viewmodel.UsuariosViewModel

// Gestión de técnicos y usuarios. Solo el administrador.
@Composable
fun UsuariosScreen(
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    usuariosViewModel: UsuariosViewModel = viewModel()
) {
    val usuarios by usuariosViewModel.usuarios.collectAsState()
    val sesionUi by sesionViewModel.estado.collectAsState()
    val sesion = sesionUi.sesion
    val esAdmin = Roles.esAdmin(sesion?.rol)
    var usuarioAEliminar by remember { mutableStateOf<Usuario?>(null) }

    PantallaConMenu(
        titulo = "Técnicos y usuarios",
        rutaActual = Screen.Usuarios.route,
        mainViewModel = mainViewModel,
        sesionViewModel = sesionViewModel,
        botonFlotante = {
            if (esAdmin) {
                ExtendedFloatingActionButton(onClick = { mainViewModel.navigateTo(Screen.usuarioForm()) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo usuario")
                }
            }
        }
    ) {
        if (!esAdmin) {
            Text(
                text = "Solo el administrador puede gestionar usuarios.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
            ) {
                items(usuarios, key = { it.id }) { usuario ->
                    val esElMismo = usuario.email == sesion?.email
                    Card(modifier = Modifier.fillMaxWidth().animateItem()) {
                        Row(
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(usuario.email, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    Roles.etiqueta(usuario.rol) + if (esElMismo) " (tú)" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                mainViewModel.navigateTo(Screen.usuarioForm(usuario.id))
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar usuario")
                            }
                            // Nadie puede eliminarse a sí mismo
                            if (!esElMismo) {
                                IconButton(onClick = { usuarioAEliminar = usuario }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar usuario",
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

    usuarioAEliminar?.let { usuario ->
        ConfirmarDialogo(
            titulo = "Eliminar usuario",
            mensaje = "Se eliminará a ${usuario.nombre}. ¿Continuar?",
            onConfirmar = {
                usuariosViewModel.eliminar(usuario, sesion?.email, sesion?.rol)
                usuarioAEliminar = null
            },
            onCancelar = { usuarioAEliminar = null }
        )
    }
}
