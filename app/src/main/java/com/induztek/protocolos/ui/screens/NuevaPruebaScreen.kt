package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.PruebaViewModel

@Composable
fun NuevaPruebaScreen(
    mainViewModel: MainViewModel,
    pruebaViewModel: PruebaViewModel = viewModel()
) {
    val state by pruebaViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = "Nueva prueba", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        CampoPrueba(
            value = state.codigoEquipo,
            onValueChange = pruebaViewModel::onCodigoEquipoChange,
            label = "Código del equipo",
            icono = Icons.Default.Build,
            error = state.codigoEquipoError
        )
        CampoPrueba(
            value = state.tension,
            onValueChange = pruebaViewModel::onTensionChange,
            label = "Tensión medida (V)",
            icono = Icons.Default.Info,
            error = state.tensionError,
            tipoTeclado = KeyboardType.Decimal
        )
        CampoPrueba(
            value = state.aislacion,
            onValueChange = pruebaViewModel::onAislacionChange,
            label = "Resistencia de aislación (MΩ)",
            icono = Icons.Default.Settings,
            error = state.aislacionError,
            tipoTeclado = KeyboardType.Decimal
        )
        CampoPrueba(
            value = state.fechaHora,
            onValueChange = pruebaViewModel::onFechaHoraChange,
            label = "Fecha y hora (dd-MM-aaaa hh:mm)",
            icono = Icons.Default.DateRange,
            error = state.fechaHoraError
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { pruebaViewModel.onGuardarClick { mainViewModel.navigateBack() } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar prueba")
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

@Composable
private fun CampoPrueba(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            if (error != null) {
                Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}