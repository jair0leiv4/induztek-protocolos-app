package com.induztek.protocolos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.induztek.protocolos.navigation.NavigationEvent
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.components.LocalAnchoVentana
import com.induztek.protocolos.ui.screens.EquipoFormScreen
import com.induztek.protocolos.ui.screens.EquiposScreen
import com.induztek.protocolos.ui.screens.LoginScreen
import com.induztek.protocolos.ui.screens.PruebaFormScreen
import com.induztek.protocolos.ui.screens.PruebasScreen
import com.induztek.protocolos.ui.screens.TiposScreen
import com.induztek.protocolos.ui.screens.UsuarioFormScreen
import com.induztek.protocolos.ui.screens.UsuariosScreen
import com.induztek.protocolos.ui.theme.InduztekProtocolosTheme
import com.induztek.protocolos.ui.utils.Animaciones
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app es siempre oscura: íconos claros en las barras del sistema
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            InduztekProtocolosTheme {
                // Tamaño de ventana: Compact (celular), Medium o Expanded
                val windowSizeClass = calculateWindowSizeClass(this@MainActivity)

                val mainViewModel: MainViewModel = viewModel()
                val sesionViewModel: SesionViewModel = viewModel()
                val sesionUi by sesionViewModel.estado.collectAsState()
                val navController = rememberNavController()

                // El portero: escucha el buzón de navegación y abre las puertas
                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { evento ->
                        when (evento) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(evento.route) {
                                    evento.popUpToRoute?.let { ruta ->
                                        popUpTo(ruta) { inclusive = evento.inclusive }
                                    }
                                    launchSingleTop = evento.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                        }
                    }
                }

                CompositionLocalProvider(LocalAnchoVentana provides windowSizeClass.widthSizeClass) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (sesionUi.cargando) {
                            // Esperando a que DataStore responda si hay una sesión guardada
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            // Si hay sesión guardada entra directo a Equipos; si no, al Login
                            val inicio = remember {
                                if (sesionUi.sesion != null) Screen.Equipos.route else Screen.Login.route
                            }

                            NavHost(
                                navController = navController,
                                startDestination = inicio,
                                enterTransition = { Animaciones.entrada },
                                exitTransition = { Animaciones.salida },
                                popEnterTransition = { Animaciones.entradaVolver },
                                popExitTransition = { Animaciones.salidaVolver }
                            ) {
                                composable(Screen.Login.route) {
                                    LoginScreen(mainViewModel)
                                }
                                composable(Screen.Equipos.route) {
                                    EquiposScreen(mainViewModel, sesionViewModel)
                                }
                                composable(Screen.Pruebas.route) {
                                    PruebasScreen(mainViewModel, sesionViewModel)
                                }
                                composable(Screen.Usuarios.route) {
                                    UsuariosScreen(mainViewModel, sesionViewModel)
                                }
                                composable(Screen.Tipos.route) {
                                    TiposScreen(mainViewModel, sesionViewModel)
                                }
                                composable(
                                    route = Screen.EquipoForm.route,
                                    arguments = listOf(
                                        navArgument(Screen.ARG_CODIGO) {
                                            type = NavType.StringType
                                            nullable = true
                                            defaultValue = null
                                        }
                                    )
                                ) { entrada ->
                                    EquipoFormScreen(
                                        codigo = entrada.arguments?.getString(Screen.ARG_CODIGO),
                                        mainViewModel = mainViewModel
                                    )
                                }
                                composable(
                                    route = Screen.PruebaForm.route,
                                    arguments = listOf(
                                        navArgument(Screen.ARG_ID) {
                                            type = NavType.LongType
                                            defaultValue = -1L
                                        }
                                    )
                                ) { entrada ->
                                    PruebaFormScreen(
                                        id = entrada.arguments?.getLong(Screen.ARG_ID) ?: -1L,
                                        mainViewModel = mainViewModel,
                                        sesionViewModel = sesionViewModel
                                    )
                                }
                                composable(
                                    route = Screen.UsuarioForm.route,
                                    arguments = listOf(
                                        navArgument(Screen.ARG_ID) {
                                            type = NavType.LongType
                                            defaultValue = -1L
                                        }
                                    )
                                ) { entrada ->
                                    UsuarioFormScreen(
                                        id = entrada.arguments?.getLong(Screen.ARG_ID) ?: -1L,
                                        mainViewModel = mainViewModel
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