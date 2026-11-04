package com.induztek.protocolos.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
        // Progreso visual de 3 pasos: datos, ubicación y foto
        val datosListos = estado.codigoEquipo.isNotBlank() &&
                estado.tension.isNotBlank() && estado.aislacion.isNotBlank()
        val ubicacionLista = estado.latitud != null && estado.longitud != null
        val fotoLista = estado.fotoPath != null
        val pasosCompletos = listOf(datosListos, ubicacionLista, fotoLista).count { it }

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
                Text(
                    text = if (estado.codigoEquipo.isBlank()) "Modo sin conexión"
                    else "${estado.codigoEquipo} · Modo sin conexión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                BarraProgreso(pasosCompletos = pasosCompletos)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$pasosCompletos de 3 pasos completos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                    label = "Aislación (MΩ)",
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

                TarjetaFoto(
                    fotoPath = estado.fotoPath,
                    onTomarFoto = { mostrarCamara = true }
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

// Barra de progreso en 3 segmentos que se pintan de lima con animación
@Composable
private fun BarraProgreso(pasosCompletos: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(3) { i ->
            val color by animateColorAsState(
                targetValue = if (i < pasosCompletos) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                label = "paso$i"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

// Tarjeta "Foto de placa" con chip Pendiente (ámbar) / Capturada (verde)
@Composable
private fun TarjetaFoto(fotoPath: String?, onTomarFoto: () -> Unit) {
    val capturada = fotoPath != null
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Foto de placa",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(
                    texto = if (capturada) "Capturada" else "Pendiente",
                    color = if (capturada) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.tertiary
                )
            }
            fotoPath?.let { ruta ->
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = File(ruta),
                    contentDescription = "Foto de la placa del equipo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onTomarFoto,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(if (capturada) "Tomar otra foto" else "Tomar foto")
            }
        }
    }
}

@Composable
private fun ChipEstado(texto: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}