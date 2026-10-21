package com.induztek.protocolos.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object Camara {

    fun crearArchivoFoto(context: Context): File {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        return File(carpeta, "placa_${System.currentTimeMillis()}.jpg")
    }

    fun uriSegura(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}