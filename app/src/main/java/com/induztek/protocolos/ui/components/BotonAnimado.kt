package com.induztek.protocolos.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.induztek.protocolos.ui.utils.rememberEscalaPresion

// Botón lima redondeado que responde al toque achicándose un poco
@Composable
fun BotonAnimado(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val presionado by interactionSource.collectIsPressedAsState()
    val escala = rememberEscalaPresion(presionado)

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .heightIn(min = 52.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold)
    }
}