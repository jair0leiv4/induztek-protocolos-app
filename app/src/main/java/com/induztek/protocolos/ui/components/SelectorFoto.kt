package com.induztek.protocolos.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.induztek.protocolos.util.Camara
import java.io.File

@Composable
fun SelectorFoto(
    fotoPath: String?,
    onFotoCapturada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var permisoDenegado by rememberSaveable { mutableStateOf(false) }

    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val ruta = rutaPendiente
        if (exito && ruta != null) {
            onFotoCapturada(ruta)
        } else if (ruta != null) {
            File(ruta).delete()
        }
        rutaPendiente = null
    }

    val lanzarCamara = {
        val archivo = Camara.crearArchivoFoto(context)
        rutaPendiente = archivo.absolutePath
        tomarFoto.launch(Camara.uriSegura(context, archivo))
    }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        permisoDenegado = !concedido
        if (concedido) lanzarCamara()
    }

    val miniatura = remember(fotoPath) {
        fotoPath?.let {
            val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
            BitmapFactory.decodeFile(it, opciones)?.asImageBitmap()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Foto de la placa del equipo", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(visible = miniatura != null) {
            if (miniatura != null) {
                Image(
                    bitmap = miniatura,
                    contentDescription = "Foto de la placa del equipo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        OutlinedButton(
            onClick = {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (tienePermiso) lanzarCamara() else pedirPermiso.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (fotoPath == null) "Tomar foto" else "Tomar otra foto")
        }

        AnimatedVisibility(visible = permisoDenegado) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Aviso",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sin permiso de cámara no se puede tomar la foto. Puedes activarlo en Ajustes del teléfono.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}