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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InduztekProtocolosTheme {
                // La profesora (ViewModel) y el control remoto de las puertas (navController)
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                // El portero: se queda escuchando el buzón
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

                // El edificio: dice qué pantalla corresponde a cada dirección
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
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
                        composable(route = Screen.Equipos.route) { EquiposScreen(viewModel) }
                        composable(route = Screen.NuevoEquipo.route) { NuevoEquipoScreen(viewModel) }
                        composable(route = Screen.NuevaPrueba.route) { NuevaPruebaScreen(viewModel) }
                    }
                }
            }
        }
    }
}