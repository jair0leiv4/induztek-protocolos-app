package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.model.Rol
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.UsuarioFormViewModel

@Composable
fun UsuarioFormScreen(
    mainViewModel: MainViewModel,
    vm: UsuarioFormViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (state.esEdicion) "Editar usuario" else "Nuevo usuario",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoValidado(
            value = state.nombre,
            onValueChange = vm::onNombreChange,
            label = "Nombre",
            icono = Icons.Default.Person,
            error = state.nombreError,
            tipoTeclado = KeyboardType.Text
        )

        if (state.esEdicion) {
            OutlinedTextField(
                value = state.correo,
                onValueChange = {},
                enabled = false,
                label = { Text("Correo (no se puede cambiar)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            CampoValidado(
                value = state.correo,
                onValueChange = vm::onCorreoChange,
                label = "Correo",
                icono = Icons.Default.Email,
                error = state.correoError,
                tipoTeclado = KeyboardType.Email
            )
        }

        CampoValidado(
            value = state.contrasena,
            onValueChange = vm::onContrasenaChange,
            label = if (state.esEdicion) "Nueva contraseña (opcional)" else "Contraseña",
            icono = Icons.Default.Lock,
            error = state.contrasenaError,
            tipoTeclado = KeyboardType.Password,
            esPassword = true
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.rol?.etiqueta ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de usuario") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                isError = state.rolError != null,
                supportingText = { state.rolError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { menuAbierto = true }
            )
            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                Rol.values().forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol.etiqueta) },
                        onClick = {
                            vm.onRolChange(rol)
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        if (state.errorGeneral != null) {
            Text(
                text = state.errorGeneral ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BotonAnimado(
            texto = if (state.guardando) "Guardando…" else "Guardar Usuario",
            onClick = { vm.guardar { mainViewModel.navigateBack() } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = { mainViewModel.navigateBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}