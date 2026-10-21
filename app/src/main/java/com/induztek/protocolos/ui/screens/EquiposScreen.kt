package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.viewmodel.EquiposViewModel
import com.induztek.protocolos.viewmodel.MainViewModel

@Composable
fun EquiposScreen(
    mainViewModel: MainViewModel,
    equiposViewModel: EquiposViewModel = viewModel()
) {
    val equipos by equiposViewModel.equipos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
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
                    Card(modifier = Modifier.fillMaxWidth()) {
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

        Button(
            onClick = { mainViewModel.navigateTo(Screen.NuevoEquipo) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrar equipo")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { mainViewModel.navigateTo(Screen.NuevaPrueba) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Nueva prueba")
        }
    }
}