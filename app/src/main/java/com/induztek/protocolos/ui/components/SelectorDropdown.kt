package com.induztek.protocolos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// Lista desplegable para elegir una opción (tipo de equipo, equipo, rol).
// Mismos estados visuales que CampoValidado: normal, válido (verde + check) y error (rojo + alerta).
@Composable
fun SelectorDropdown(
    label: String,
    opciones: List<String>,
    seleccionado: String,
    onSeleccion: (String) -> Unit,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val hayError = error != null
    val valido = !hayError && seleccionado.isNotBlank()
    val colorOk = MaterialTheme.colorScheme.secondary

    Box(modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label.uppercase()) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                when {
                    hayError -> Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    valido -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Valor válido",
                        tint = colorOk
                    )
                    else -> Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir lista")
                }
            },
            isError = hayError,
            supportingText = {
                when {
                    hayError -> Text(error ?: "")
                    valido -> Text("Valor válido", color = colorOk)
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (valido) colorOk else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (valido) colorOk else MaterialTheme.colorScheme.outline,
                focusedLabelColor = if (valido) colorOk else MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente: al tocar el campo se abre la lista
        Box(modifier = Modifier.matchParentSize().clickable { expandido = true })

        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            if (opciones.isEmpty()) {
                DropdownMenuItem(text = { Text("Sin opciones disponibles") }, onClick = { expandido = false })
            }
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}