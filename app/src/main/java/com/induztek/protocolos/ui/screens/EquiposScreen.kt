package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.viewmodel.EquiposViewModel
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel

@Composable
fun EquiposScreen(
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    equiposViewModel: EquiposViewModel = viewModel()
) {
    val equipos by equiposViewModel.equipos.collectAsState()
    val sesionUi by sesionViewModel.estado.collectAsState()
    val sesion = sesionUi.sesion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hola, ${sesion?.nombre ?: ""}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Rol: ${Roles.etiqueta(sesion?.rol ?: "")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(
                onClick = {
                    sesionViewModel.cerrarSesion()
                    mainViewModel.navigateTo(
                        Screen.Login,
                        popUpTo = Screen.Equipos,
                        inclusive = true
                    )
                }
            ) {
                Text(text = "Salir")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Tus equipos", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "${equipos.size} registrados",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (equipos.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = "Aún no hay equipos. Registra el primero.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(equipos, key = { it.codigo }) { equipo ->
                    Card(modifier = Modifier.fillMaxWidth().animateItem()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = equipo.codigo, style = MaterialTheme.typography.titleMedium)
                            Text(text = equipo.tipo, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = equipo.instalacion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BotonAnimado(
            texto = "Registrar equipo",
            onClick = { mainViewModel.navigateTo(Screen.NuevoEquipo) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { mainViewModel.navigateTo(Screen.NuevaPrueba) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Nueva prueba")
        }
    }
}