package com.induztek.protocolos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ámbar del chip "Revisar" (no existe en Color.kt)
val Ambar = Color(0xFFFBBF24)
val AmbarOscuro = Color(0xFFB45309)

private val EsquemaOscuro = darkColorScheme(
    primary = Lima,
    onPrimary = Carbon,
    secondary = VerdeOk,
    onSecondary = Carbon,
    tertiary = Ambar,
    onTertiary = Carbon,
    background = Carbon,
    onBackground = TextoClaro,
    surface = CarbonSuperficie,
    onSurface = TextoClaro,
    surfaceVariant = CarbonVariante,
    onSurfaceVariant = TextoApagado,
    outline = BordeOscuro,
    error = ErrorClaro,
    onError = Carbon
)

private val EsquemaClaro = lightColorScheme(
    primary = VerdeOscuro,
    onPrimary = FondoClaro,
    secondary = VerdeOkOscuro,
    onSecondary = FondoClaro,
    tertiary = AmbarOscuro,
    onTertiary = FondoClaro,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = FondoClaro,
    onSurface = TextoOscuro,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = GrisTexto,
    outline = BordeClaro,
    error = ErrorOscuro,
    onError = FondoClaro
)

@Composable
fun InduztekProtocolosTheme(
    // El diseño de los mockups es oscuro: se usa por defecto,
    // sin depender del modo del sistema.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}