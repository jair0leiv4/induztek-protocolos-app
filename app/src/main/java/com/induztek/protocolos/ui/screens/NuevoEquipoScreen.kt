package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.MainViewModel

@Composable
fun NuevoEquipoScreen(
    mainViewModel: MainViewModel,
    equipoViewModel: EquipoViewModel = viewModel()
) {
    val state by equipoViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = "Registrar equipo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = state.codigo,
            onValueChange = equipoViewModel::onCodigoChange,
            label = { Text("Código del equipo") },
            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
            trailingIcon = {
                if (state.codigoError != null) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                }
            },
            isError = state.codigoError != null,
            supportingText = { state.codigoError?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Tipo de equipo", style = MaterialTheme.typography.titleSmall)

        equipoViewModel.tiposDisponibles.forEach { tipo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = state.tipo == tipo,
                        onClick = { equipoViewModel.onTipoChange(tipo) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = state.tipo == tipo, onClick = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = tipo)
            }
        }

        if (state.tipoError != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = state.tipoError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.instalacion,
            onValueChange = equipoViewModel::onInstalacionChange,
            label = { Text("Instalación del cliente") },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            trailingIcon = {
                if (state.instalacionError != null) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                }
            },
            isError = state.instalacionError != null,
            supportingText = { state.instalacionError?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { equipoViewModel.onGuardarClick { mainViewModel.navigateBack() } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar equipo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { mainViewModel.navigateBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cancelar")
        }
    }
}