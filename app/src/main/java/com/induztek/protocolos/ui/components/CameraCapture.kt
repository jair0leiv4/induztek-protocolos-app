package com.induztek.protocolos.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

// RECURSO NATIVO 1: cámara con CameraX (vista previa, cambio de lente y foto guardada en almacenamiento privado)
@Composable
fun CameraCapture(
    onFotoTomada: (String) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ---- Permiso de cámara (se pide en el momento de usarla) ----
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permisoDenegado by remember { mutableStateOf(false) }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermiso = concedido
        permisoDenegado = !concedido
    }

    LaunchedEffect(Unit) {
        if (!tienePermiso) lanzadorPermiso.launch(Manifest.permission.CAMERA)
    }

    if (!tienePermiso) {
        // Sin permiso: la app no se cae, explica qué pasó y ofrece alternativas
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (permisoDenegado) {
                    "Sin permiso de cámara no se puede tomar la foto de la placa."
                } else {
                    "Se necesita permiso para usar la cámara."
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { lanzadorPermiso.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Pedir permiso de nuevo") }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Abrir ajustes de la app") }
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
        return
    }

    // ---- Cámara ----
    var lente by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var proveedor by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    val imageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }
    val previewView = remember { PreviewView(context) }

    // Conecta la cámara con la vista previa cada vez que cambia el lente
    LaunchedEffect(lente) {
        val futuro = ProcessCameraProvider.getInstance(context)
        futuro.addListener({
            try {
                val provider = futuro.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val selector = CameraSelector.Builder().requireLensFacing(lente).build()
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                proveedor = provider
                mensaje = null
            } catch (e: Exception) {
                mensaje = "No se pudo abrir la cámara."
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Al salir de esta pantalla se libera la cámara
    DisposableEffect(Unit) {
        onDispose { proveedor?.unbindAll() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            mensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancelar) {
                    Icon(Icons.Default.Close, contentDescription = "Cancelar", modifier = Modifier.size(32.dp))
                }
                FloatingActionButton(
                    onClick = {
                        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
                        val archivo = File(carpeta, "placa_${System.currentTimeMillis()}.jpg")
                        val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
                        imageCapture.takePicture(
                            opciones,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    onFotoTomada(archivo.absolutePath)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    mensaje = "No se pudo tomar la foto. Intenta de nuevo."
                                }
                            }
                        )
                    }
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Tomar foto")
                }
                IconButton(
                    onClick = {
                        lente = if (lente == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    }
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Cambiar cámara", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
