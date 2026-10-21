package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.ui.components.SelectorFoto
import com.induztek.protocolos.ui.components.SelectorUbicacion
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

        CampoValidado(
            value = state.codigoEquipo,
            onValueChange = pruebaViewModel::onCodigoEquipoChange,
            label = "Código del equipo",
            icono = Icons.Default.Build,
            error = state.codigoEquipoError
        )
        CampoValidado(
            value = state.tension,
            onValueChange = pruebaViewModel::onTensionChange,
            label = "Tensión medida (V)",
            icono = Icons.Default.Info,
            error = state.tensionError,
            tipoTeclado = KeyboardType.Decimal
        )
        CampoValidado(
            value = state.aislacion,
            onValueChange = pruebaViewModel::onAislacionChange,
            label = "Resistencia de aislación (MΩ)",
            icono = Icons.Default.Settings,
            error = state.aislacionError,
            tipoTeclado = KeyboardType.Decimal
        )
        CampoValidado(
            value = state.fechaHora,
            onValueChange = pruebaViewModel::onFechaHoraChange,
            label = "Fecha y hora (dd-MM-aaaa hh:mm)",
            icono = Icons.Default.DateRange,
            error = state.fechaHoraError
        )

        Spacer(modifier = Modifier.height(8.dp))

        SelectorUbicacion(
            latitud = state.latitud,
            longitud = state.longitud,
            onUbicacionCapturada = pruebaViewModel::onUbicacionCapturada
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectorFoto(
            fotoPath = state.fotoPath,
            onFotoCapturada = pruebaViewModel::onFotoCapturada
        )

        Spacer(modifier = Modifier.height(16.dp))

        BotonAnimado(
            texto = "Guardar prueba",
            onClick = { pruebaViewModel.onGuardarClick { mainViewModel.navigateBack() } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { mainViewModel.navigateBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cancelar")
        }
    }
}