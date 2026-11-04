package com.induztek.protocolos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        ContenedorAdaptable(modifier = Modifier.padding(padding)) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Línea quebrada decorativa (mockup)
                LineaDecorativa(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(90.dp)
                )

                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "INDUZTEK",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Protocolos en terreno.",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(28.dp))

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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Funciona sin conexión",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Cuentas de demostración",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
}

@Composable
private fun LineaDecorativa(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val camino = Path().apply {
            moveTo(0f, h * 0.70f)
            lineTo(w * 0.18f, h * 0.70f)
            lineTo(w * 0.28f, h * 0.30f)
            lineTo(w * 0.42f, h * 0.30f)
            lineTo(w * 0.52f, h * 0.60f)
            lineTo(w * 0.70f, h * 0.60f)
            lineTo(w * 0.80f, h * 0.15f)
            lineTo(w, h * 0.15f)
        }
        drawPath(
            path = camino,
            color = color.copy(alpha = 0.55f),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}