package com.induztek.protocolos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.induztek.protocolos.ui.components.ContenedorAdaptable
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.MainViewModel

// Un solo login: según el usuario, entra como Administrador o como Técnico
@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    loginViewModel: LoginViewModel = viewModel()
) {
    val state by loginViewModel.uiState.collectAsState()

    Scaffold { padding ->
        ContenedorAdaptable(modifier = Modifier.padding(padding)) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Protocolos en terreno", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "Induztek Ingeniería",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

                    AnimatedVisibility(visible = state.loginError != null) {
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.loginError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BotonAnimado(
                        texto = if (state.cargando) "Ingresando…" else "Ingresar",
                        enabled = !state.cargando,
                        onClick = {
                            loginViewModel.onLoginClick {
                                mainViewModel.navigateTo(
                                    route = Screen.Equipos.route,
                                    popUpTo = Screen.Login.route,
                                    inclusive = true
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Cuentas de demostración", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "Administrador: admin@induztek.cl / Admin123",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Técnico: tecnico@induztek.cl / Tecnico123",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
