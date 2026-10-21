package com.induztek.protocolos.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

object Ubicacion {

    @SuppressLint("MissingPermission")
    fun obtener(
        context: Context,
        onResultado: (Double, Double) -> Unit,
        onError: () -> Unit
    ) {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val proveedor = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> {
                onError()
                return
            }
        }

        try {
            LocationManagerCompat.getCurrentLocation(
                manager,
                proveedor,
                null,
                ContextCompat.getMainExecutor(context)
            ) { location ->
                if (location != null) {
                    onResultado(location.latitude, location.longitude)
                } else {
                    onError()
                }
            }
        } catch (e: SecurityException) {
            onError()
        }
    }
}