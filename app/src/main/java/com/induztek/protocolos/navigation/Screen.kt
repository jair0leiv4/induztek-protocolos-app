package com.induztek.protocolos.navigation

sealed class Screen (val route:String){
    data object  Login : Screen (route = "login")
    data object  Equipos : Screen (route ="equipos")
    data object NuevoEquipo : Screen  (route = "nuevo_equipo")
    data object  NuevaPrueba : Screen (route = "nueva_prueba")
}