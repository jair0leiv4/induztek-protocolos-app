
package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.model.Rol
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.util.EdicionUsuario
import com.induztek.protocolos.util.Sesion
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.UsuarioListViewModel
import kotlinx.coroutines.delay

@Composable
fun UsuarioListScreen(
    mainViewModel: MainViewModel,
    vm: UsuarioListViewModel = viewModel()
) {
    val usuarios by vm.usuarios.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val sesion by Sesion.usuario.collectAsState()
    var aEliminar by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            delay(3000)
            vm.mensajeMostrado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = { mainViewModel.navigateBack() }) { Text("← Volver") }
        Text(text = "Usuarios", style = MaterialTheme.typography.headlineSmall)

        if (sesion?.obtenerRol() != Rol.ADMIN) {
            Text("Solo los administradores pueden gestionar usuarios.")
            return@Column
        }

        mensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(usuarios, key = { it.correo }) { usuario ->
                TarjetaUsuario(
                    usuario = usuario,
                    onEditar = {
                        EdicionUsuario.correo = usuario.correo
                        mainViewModel.navigateTo(Screen.FormularioUsuario)
                    },
                    onEliminar = { aEliminar = usuario }
                )
            }
        }

        BotonAnimado(
            texto = "Agregar usuario",
            onClick = {
                EdicionUsuario.correo = null
                mainViewModel.navigateTo(Screen.FormularioUsuario)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    aEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que quieres eliminar a ${usuario.nombre} (${usuario.correo})?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(usuario)
                    aEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { aEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: Usuario,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = usuario.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = usuario.correo, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = usuario.obtenerRol().etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar usuario")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar usuario")
            }
        }
    }
}