package com.induztek.protocolos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.BotonAnimado
import com.induztek.protocolos.ui.components.CampoValidado
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    loginViewModel: LoginViewModel = viewModel()
) {
    val state by loginViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Protocolos en terreno", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        CampoValidado(
            value = state.email,
            onValueChange = loginViewModel::onEmailChange,
            label = "Correo",
            icono = Icons.Default.Email,
            error = state.emailError,
            tipoTeclado = KeyboardType.Email
        )
        CampoValidado(
            value = state.password,
            onValueChange = loginViewModel::onPasswordChange,
            label = "Contraseña",
            icono = Icons.Default.Lock,
            error = state.passwordError,
            tipoTeclado = KeyboardType.Password,
            esPassword = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        BotonAnimado(
            texto = "Ingresar",
            onClick = { loginViewModel.onLoginClick { mainViewModel.navigateTo(Screen.Equipos) } },
            modifier = Modifier.fillMaxWidth()
        )
    }
}