package com.induztek.protocolos.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.ui.components.CameraCapture
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.ui.components.PantallaConAtras
import com.induztek.protocolos.ui.components.SelectorDropdown
import com.induztek.protocolos.ui.components.SelectorUbicacion
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.PruebaFormViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import java.io.File

// Registrar (CREATE) o editar (UPDATE) una prueba, con ubicación y foto de la placa
@Composable
fun PruebaFormScreen(
    id: Long,
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    vm: PruebaFormViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val equipos by vm.equipos.collectAsState()
    val sesionUi by sesionViewModel.estado.collectAsState()
    var mostrarCamara by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(id) { vm.cargar(id) }

    if (mostrarCamara) {
        // La cámara ocupa toda la pantalla. "Atrás" la cierra sin salir del formulario.
        BackHandler { mostrarCamara = false }
        CameraCapture(
            onFotoTomada = { ruta ->
                vm.onFotoCapturada(ruta)
                mostrarCamara = false
            },
            onCancelar = { mostrarCamara = false }
        )
    } else {
        PantallaConAtras(
            titulo = if (estado.esEdicion) "Editar prueba" else "Nueva prueba",
            onAtras = { mainViewModel.navigateBack() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp)
            ) {
                if (equipos.isEmpty()) {
                    Text(
                        text = "Primero registra un equipo para poder asociarle pruebas.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                SelectorDropdown(
                    label = "Equipo",
                    opciones = equipos,
                    seleccionado = estado.codigoEquipo,
                    onSeleccion = vm::onCodigoEquipoChange,
                    icono = Icons.Default.Build,
                    error = estado.codigoEquipoError
                )
                CampoValidado(
                    value = estado.tension,
                    onValueChange = vm::onTensionChange,
                    label = "Tensión medida (V)",
                    icono = Icons.Default.Info,
                    error = estado.tensionError,
                    tipoTeclado = KeyboardType.Decimal
                )
                CampoValidado(
                    value = estado.aislacion,
                    onValueChange = vm::onAislacionChange,
                    label = "Resistencia de aislación (MΩ)",
                    icono = Icons.Default.Settings,
                    error = estado.aislacionError,
                    tipoTeclado = KeyboardType.Decimal
                )
                CampoValidado(
                    value = estado.fechaHora,
                    onValueChange = vm::onFechaHoraChange,
                    label = "Fecha y hora (dd-MM-aaaa hh:mm)",
                    icono = Icons.Default.DateRange,
                    error = estado.fechaHoraError
                )

                Spacer(modifier = Modifier.height(8.dp))

                SelectorUbicacion(
                    latitud = estado.latitud,
                    longitud = estado.longitud,
                    onUbicacionCapturada = vm::onUbicacionCapturada
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Foto de la placa del equipo", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                estado.fotoPath?.let { ruta ->
                    AsyncImage(
                        model = File(ruta),
                        contentDescription = "Foto de la placa del equipo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                OutlinedButton(
                    onClick = { mostrarCamara = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (estado.fotoPath == null) "Tomar foto" else "Tomar otra foto")
                }

                Spacer(modifier = Modifier.height(16.dp))

                BotonAnimado(
                    texto = "Guardar prueba",
                    onClick = {
                        vm.onGuardarClick(sesionUi.sesion?.email ?: "") { mainViewModel.navigateBack() }
                    },
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
}
