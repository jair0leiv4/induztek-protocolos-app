package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.induztek.protocolos.viewmodel.MainViewModel

@Composable
fun NuevaPruebaScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nueva prueba")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.navigateBack() }) {
            Text(text = "Volver")
        }
    }
}