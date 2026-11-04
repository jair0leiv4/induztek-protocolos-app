package com.induztek.protocolos.navigation

// Los "papelitos" que el ViewModel deja en el buzón para que MainActivity los ejecute
sealed class NavigationEvent {
    data class NavigateTo(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
}
