package com.induztek.protocolos.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.induztek.protocolos.ui.utils.IconoErrorAnimado

// Campo de texto con ícono, borde y mensaje de estado.
// No valida: solo muestra lo que le dice el ViewModel.
// Estados visuales: normal, válido (verde + check) y error (rojo + alerta).
@Composable
fun CampoValidado(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icono: ImageVector,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false,
    habilitado: Boolean = true,
    modifier: Modifier = Modifier
) {
    val hayError = error != null
    val valido = !hayError && value.isNotBlank()
    val colorOk = MaterialTheme.colorScheme.secondary

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label.uppercase()) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            when {
                hayError -> IconoErrorAnimado(visible = true)
                valido -> Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Valor válido",
                    tint = colorOk
                )
            }
        },
        isError = hayError,
        supportingText = {
            when {
                hayError -> Text(error ?: "")
                valido -> Text("Valor válido", color = colorOk)
            }
        },
        enabled = habilitado,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (valido) colorOk else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (valido) colorOk else MaterialTheme.colorScheme.outline,
            focusedLabelColor = if (valido) colorOk else MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        visualTransformation = if (esPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}