package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.ConfirmarDialogo
import com.induztek.protocolos.ui.components.PantallaConMenu
import com.induztek.protocolos.util.Fechas
import com.induztek.protocolos.util.Formato
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.PruebaItem
import com.induztek.protocolos.viewmodel.PruebasViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import java.io.File
import java.util.Locale

// Historial de pruebas: compara cada medición con la anterior del mismo equipo
@Composable
fun PruebasScreen(
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    pruebasViewModel: PruebasViewModel = viewModel()
) {
    val items by pruebasViewModel.items.collectAsState()
    val filtro by pruebasViewModel.filtro.collectAsState()
    val sesionUi by sesionViewModel.estado.collectAsState()
    val rol = sesionUi.sesion?.rol
    val puedeGestionar = Roles.puedeEditarYEliminar(rol)
    var pruebaAEliminar by remember { mutableStateOf<PruebaItem?>(null) }

    PantallaConMenu(
        titulo = "Historial de pruebas",
        rutaActual = Screen.Pruebas.route,
        mainViewModel = mainViewModel,
        sesionViewModel = sesionViewModel,
        botonFlotante = {
            ExtendedFloatingActionButton(
                onClick = { mainViewModel.navigateTo(Screen.pruebaForm()) },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva prueba", fontWeight = FontWeight.Bold)
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = filtro,
                onValueChange = pruebasViewModel::onFiltroChange,
                label = { Text("Filtrar por código de equipo") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${items.size} PRUEBAS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (items.isEmpty()) {
                Text(
                    text = "Todavía no hay pruebas registradas.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(items, key = { it.prueba.id }) { item ->
                        val p = item.prueba
                        Card(
                            modifier = Modifier.fillMaxWidth().animateItem(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            p.codigoEquipo,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        item.variacionAislacion?.let { v ->
                                            val color = if (v < 0) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.primary
                                            ChipVariacion(texto = Formato.porcentaje(v), color = color)
                                        }
                                    }
                                    Text(
                                        Fechas.formatear(p.fechaMillis),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "TENSIÓN",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                "${Formato.numero(p.tension)} V",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "AISLACIÓN",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                "${Formato.numero(p.aislacion)} MΩ",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    item.variacionAislacion?.let { v ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val texto = if (v < 0) {
                                            "Aislación bajó ${Formato.porcentaje(v)} respecto a la medición anterior"
                                        } else {
                                            "Aislación subió ${Formato.porcentaje(v)} respecto a la medición anterior"
                                        }
                                        Text(
                                            texto,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (v < 0) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (p.latitud != null && p.longitud != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Ubicación: ${"%.4f".format(Locale.US, p.latitud)}, ${"%.4f".format(Locale.US, p.longitud)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (p.registradaPor.isNotBlank()) {
                                        Text(
                                            "Registrada por ${p.registradaPor}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                p.fotoPath?.let { ruta ->
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AsyncImage(
                                        model = File(ruta),
                                        contentDescription = "Foto de la placa del equipo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
                                    )
                                }
                                if (puedeGestionar) {
                                    Column {
                                        IconButton(onClick = {
                                            mainViewModel.navigateTo(Screen.pruebaForm(p.id))
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar prueba")
                                        }
                                        IconButton(onClick = { pruebaAEliminar = item }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar prueba",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pruebaAEliminar?.let { item ->
        ConfirmarDialogo(
            titulo = "Eliminar prueba",
            mensaje = "Se eliminará la prueba de ${item.prueba.codigoEquipo}. ¿Continuar?",
            onConfirmar = {
                pruebasViewModel.eliminar(item.prueba, rol)
                pruebaAEliminar = null
            },
            onCancelar = { pruebaAEliminar = null }
        )
    }
}

@Composable
private fun ChipVariacion(texto: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
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