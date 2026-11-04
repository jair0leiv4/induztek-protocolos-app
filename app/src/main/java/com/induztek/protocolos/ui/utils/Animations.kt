package com.induztek.protocolos.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

// ANIMACIÓN 1: transiciones entre pantallas (fundido + deslizamiento suave)
object Animaciones {
    val entrada: EnterTransition =
        fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 6 }
    val salida: ExitTransition =
        fadeOut(tween(200))
    val entradaVolver: EnterTransition =
        fadeIn(tween(300))
    val salidaVolver: ExitTransition =
        fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 6 }
}

// ANIMACIÓN 2: el ícono de error aparece "creciendo" y desaparece achicándose
@Composable
fun IconoErrorAnimado(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

// ANIMACIÓN 3: el botón se achica un poquito mientras se mantiene apretado
@Composable
fun rememberEscalaPresion(presionado: Boolean): Float {
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "escalaBoton"
    )
    return escala
}

// ANIMACIÓN 4 (en las listas): Modifier.animateItem() hace que las tarjetas se acomoden suavemente
