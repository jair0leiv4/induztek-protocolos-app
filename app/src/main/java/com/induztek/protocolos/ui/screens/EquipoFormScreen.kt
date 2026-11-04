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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.ui.components.PantallaConAtras
import com.induztek.protocolos.ui.components.SelectorDropdown
import com.induztek.protocolos.viewmodel.EquipoFormViewModel
import com.induztek.protocolos.viewmodel.MainViewModel

// Crear (CREATE) o editar (UPDATE) un equipo
@Composable
fun EquipoFormScreen(
    codigo: String?,
    mainViewModel: MainViewModel,
    vm: EquipoFormViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val tipos by vm.tipos.collectAsState()

    LaunchedEffect(codigo) { vm.cargar(codigo) }

    PantallaConAtras(
        titulo = if (estado.esEdicion) "Editar equipo" else "Registrar equipo",
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
                value = estado.codigo,
                onValueChange = vm::onCodigoChange,
                label = "Código del equipo",
                icono = Icons.Default.Build,
                error = estado.codigoError,
                habilitado = !estado.esEdicion
            )
            SelectorDropdown(
                label = "Tipo de equipo",
                opciones = tipos,
                seleccionado = estado.tipo,
                onSeleccion = vm::onTipoChange,
                icono = Icons.Default.Settings,
                error = estado.tipoError
            )
            CampoValidado(
                value = estado.instalacion,
                onValueChange = vm::onInstalacionChange,
                label = "Instalación del cliente",
                icono = Icons.Default.Place,
                error = estado.instalacionError
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonAnimado(
                texto = "Guardar equipo",
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
