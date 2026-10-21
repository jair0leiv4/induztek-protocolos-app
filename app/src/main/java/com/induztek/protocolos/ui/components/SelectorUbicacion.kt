package com.induztek.protocolos.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.induztek.protocolos.util.Ubicacion
import java.util.Locale

@Composable
fun SelectorUbicacion(
    latitud: Double?,
    longitud: Double?,
    onUbicacionCapturada: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cargando by rememberSaveable { mutableStateOf(false) }
    var mensajeError by rememberSaveable { mutableStateOf<String?>(null) }

    val obtenerUbicacion: () -> Unit = {
        cargando = true
        mensajeError = null
        Ubicacion.obtener(
            context = context,
            onResultado = { lat, lon ->
                cargando = false
                onUbicacionCapturada(lat, lon)
            },
            onError = {
                cargando = false
                mensajeError = "No se pudo obtener la ubicación. Revisa que el GPS esté activado."
            }
        )
    }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            obtenerUbicacion()
        } else {
            mensajeError = "Sin permiso de ubicación no se puede registrar dónde se hizo la prueba. Puedes activarlo en Ajustes del teléfono."
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Ubicación de la instalación", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(visible = latitud != null && longitud != null) {
            if (latitud != null && longitud != null) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Ubicación capturada",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lat ${"%.5f".format(Locale.US, latitud)}, Lon ${"%.5f".format(Locale.US, longitud)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        OutlinedButton(
            onClick = {
                val tienePermiso = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).any {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                if (tienePermiso) {
                    obtenerUbicacion()
                } else {
                    pedirPermiso.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Obteniendo ubicación…")
            } else {
                Icon(Icons.Default.Place, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (latitud == null) "Capturar ubicación" else "Actualizar ubicación")
            }
        }

        AnimatedVisibility(visible = mensajeError != null) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Aviso",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = mensajeError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}