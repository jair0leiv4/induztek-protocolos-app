package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.ui.components.PantallaConAtras
import com.induztek.protocolos.ui.components.SelectorDropdown
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.UsuarioFormViewModel

// Crear (CREATE) o editar (UPDATE) un usuario: administrador o técnico
@Composable
fun UsuarioFormScreen(
    id: Long,
    mainViewModel: MainViewModel,
    vm: UsuarioFormViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()

    LaunchedEffect(id) { vm.cargar(id) }

    PantallaConAtras(
        titulo = if (estado.esEdicion) "Editar usuario" else "Nuevo usuario",
        onAtras = { mainViewModel.navigateBack() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            CampoValidado(
                value = estado.nombre,
                onValueChange = vm::onNombreChange,
                label = "Nombre",
                icono = Icons.Default.Person,
                error = estado.nombreError
            )
            CampoValidado(
                value = estado.email,
                onValueChange = vm::onEmailChange,
                label = "Correo",
                icono = Icons.Default.Email,
                error = estado.emailError,
                tipoTeclado = KeyboardType.Email
            )
            CampoValidado(
                value = estado.password,
                onValueChange = vm::onPasswordChange,
                label = if (estado.esEdicion) "Nueva contraseña (opcional)" else "Contraseña",
                icono = Icons.Default.Lock,
                error = estado.passwordError,
                tipoTeclado = KeyboardType.Password,
                esPassword = true
            )
            SelectorDropdown(
                label = "Rol",
                opciones = Roles.etiquetas,
                seleccionado = Roles.etiqueta(estado.rol),
                onSeleccion = vm::onRolChange,
                icono = Icons.Default.Settings,
                error = estado.rolError
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonAnimado(
                texto = "Guardar usuario",
                onClick = { vm.onGuardarClick { mainViewModel.navigateBack() } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { mainViewModel.navigateBack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
