package com.induztek.protocolos.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.navigation.Screen
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

// Tamaño de ventana actual (Compact = celular). Lo calcula MainActivity con WindowSizeClass.
val LocalAnchoVentana = compositionLocalOf { WindowWidthSizeClass.Compact }

// En pantalla Compact usa todo el ancho. En pantallas más grandes limita el contenido a 600 dp, centrado.
@Composable
fun ContenedorAdaptable(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    val ancho = LocalAnchoVentana.current
    if (ancho == WindowWidthSizeClass.Compact) {
        Box(modifier = modifier.fillMaxSize()) { contenido() }
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxHeight()) { contenido() }
        }
    }
}

// Pantalla principal con menú lateral (hamburguesa). El menú cambia según el rol.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConMenu(
    titulo: String,
    rutaActual: String,
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    botonFlotante: @Composable () -> Unit = {},
    contenido: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sesionUi by sesionViewModel.estado.collectAsState()
    val sesion = sesionUi.sesion
    val esAdmin = Roles.esAdmin(sesion?.rol)

    fun ir(ruta: String) {
        scope.launch { drawerState.close() }
        mainViewModel.navigateTo(
            route = ruta,
            popUpTo = Screen.Equipos.route,
            inclusive = false,
            singleTop = true
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                    Text("Induztek Protocolos", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(sesion?.nombre ?: "", style = MaterialTheme.typography.titleSmall)
                    Text(
                        sesion?.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Rol: ${Roles.etiqueta(sesion?.rol ?: "")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                ItemMenu("Equipos", Icons.Default.Build, rutaActual == Screen.Equipos.route) {
                    ir(Screen.Equipos.route)
                }
                ItemMenu("Historial de pruebas", Icons.Default.DateRange, rutaActual == Screen.Pruebas.route) {
                    ir(Screen.Pruebas.route)
                }
                if (esAdmin) {
                    ItemMenu("Técnicos y usuarios", Icons.Default.Person, rutaActual == Screen.Usuarios.route) {
                        ir(Screen.Usuarios.route)
                    }
                    ItemMenu("Tipos de equipo", Icons.Default.Settings, rutaActual == Screen.Tipos.route) {
                        ir(Screen.Tipos.route)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ItemMenu("Cerrar sesión", Icons.AutoMirrored.Filled.ExitToApp, false) {
                    scope.launch { drawerState.close() }
                    sesionViewModel.cerrarSesion()
                    mainViewModel.navigateTo(
                        route = Screen.Login.route,
                        popUpTo = Screen.Equipos.route,
                        inclusive = true
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            },
            floatingActionButton = botonFlotante
        ) { padding ->
            ContenedorAdaptable(modifier = Modifier.padding(padding)) { contenido() }
        }
    }
}

@Composable
private fun ItemMenu(texto: String, icono: ImageVector, seleccionado: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(texto) },
        icon = { Icon(icono, contentDescription = null) },
        selected = seleccionado,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

// Pantalla secundaria (formularios) con flecha para volver
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConAtras(
    titulo: String,
    onAtras: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        ContenedorAdaptable(modifier = Modifier.padding(padding)) { contenido() }
    }
}
