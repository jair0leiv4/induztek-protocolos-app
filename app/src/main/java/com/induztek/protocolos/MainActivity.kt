package com.induztek.protocolos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.induztek.protocolos.navigation.NavigationEvent
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.ui.screens.EquiposScreen
import com.induztek.protocolos.ui.screens.LoginScreen
import com.induztek.protocolos.ui.screens.NuevaPruebaScreen
import com.induztek.protocolos.ui.screens.NuevoEquipoScreen
import com.induztek.protocolos.ui.theme.InduztekProtocolosTheme
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InduztekProtocolosTheme {
                val viewModel: MainViewModel = viewModel()
                val sesionViewModel: SesionViewModel = viewModel()
                val sesionUi by sesionViewModel.estado.collectAsState()
                val navController = rememberNavController()

                // El portero: escucha el buzón de navegación
                LaunchedEffect(key1 = Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(route = event.route.route) {
                                    event.popUpToRoute?.let {
                                        popUpTo(it.route) { inclusive = event.inclusive }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (sesionUi.cargando) {
                        // Esperando a que DataStore responda si hay una sesión guardada
                        Box(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Pantalla inicial: si hay sesión guardada va directo a Equipos, si no, al Login
                        val inicio = remember {
                            if (sesionUi.sesion != null) Screen.Equipos.route else Screen.Login.route
                        }

                        NavHost(
                            navController = navController,
                            startDestination = inicio,
                            modifier = Modifier.padding(paddingValues = innerPadding),
                            enterTransition = {
                                fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 6 }
                            },
                            exitTransition = { fadeOut(tween(200)) },
                            popEnterTransition = { fadeIn(tween(300)) },
                            popExitTransition = {
                                fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 6 }
                            }
                        ) {
                            composable(route = Screen.Login.route) { LoginScreen(viewModel) }
                            composable(route = Screen.Equipos.route) { EquiposScreen(viewModel, sesionViewModel) }
                            composable(route = Screen.NuevoEquipo.route) { NuevoEquipoScreen(viewModel) }
                            composable(route = Screen.NuevaPrueba.route) { NuevaPruebaScreen(viewModel) }
                        }
                    }
                }
            }
        }
    }
}