package com.induztek.protocolos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaOscuro = darkColorScheme(
    primary = Lima,
    onPrimary = Carbon,
    secondary = VerdeOk,
    onSecondary = Carbon,
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
