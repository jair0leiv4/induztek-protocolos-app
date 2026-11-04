package com.induztek.protocolos.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.protocolos.util.Fechas
import com.induztek.protocolos.util.Formato
import com.induztek.protocolos.viewmodel.MainViewModel
import com.induztek.protocolos.viewmodel.PruebasViewModel

// Detalle de un equipo: aislación actual, gráfico de tendencia e historial de sus pruebas
@Composable
fun EquipoDetalleScreen(
    codigo: String,
    mainViewModel: MainViewModel,
    pruebasViewModel: PruebasViewModel = viewModel()
) {
    val context = LocalContext.current
    val todos by pruebasViewModel.items.collectAsState()

    LaunchedEffect(codigo) { pruebasViewModel.onFiltroChange(codigo) }

    // Solo las pruebas de este equipo, de la más reciente a la más antigua
    val items = todos
        .filter { it.prueba.codigoEquipo == codigo }
        .sortedByDescending { it.prueba.fechaMillis }
    val ultima = items.firstOrNull()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { mainViewModel.navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                Column {
                    Text(codigo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Detalle del equipo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ultima == null) {
                Text(
                    "Este equipo todavía no tiene pruebas registradas.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    "AISLACIÓN ACTUAL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${Formato.numero(ultima.prueba.aislacion)} MΩ",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            ultima.variacionAislacion?.let { v ->
                                val color = if (v < 0) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = color.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        Formato.porcentaje(v),
                                        color = color,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val valores = items.reversed().takeLast(8).map { it.prueba.aislacion }
                        if (valores.size >= 2) {
                            GraficoLinea(
                                valores = valores,
                                color = MaterialTheme.colorScheme.primary,
                                colorGuia = MaterialTheme.colorScheme.outlineVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    Fechas.formatear(items.reversed().takeLast(8).first().prueba.fechaMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    Fechas.formatear(ultima.prueba.fechaMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                "Se necesitan al menos 2 pruebas para mostrar la tendencia.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(Fechas.formatear(item.prueba.fechaMillis), style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${Formato.numero(item.prueba.tension)} V",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${Formato.numero(item.prueba.aislacion)} MΩ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val p = ultima.prueba
                        val texto = buildString {
                            appendLine("Equipo $codigo")
                            appendLine("Fecha: ${Fechas.formatear(p.fechaMillis)}")
                            appendLine("Tensión: ${Formato.numero(p.tension)} V")
                            append("Aislación: ${Formato.numero(p.aislacion)} MΩ")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, texto)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir prueba"))
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Compartir", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GraficoLinea(valores: List<Double>, color: Color, colorGuia: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val margen = 12.dp.toPx()
        val ancho = size.width - margen * 2
        val alto = size.height - margen * 2
        val minimo = valores.min()
        val rango = (valores.max() - minimo).takeIf { it > 0 } ?: 1.0
        val n = valores.size

        fun punto(i: Int): Offset {
            val x = margen + ancho * i / (n - 1)
            val y = margen + alto * (1f - ((valores[i] - minimo) / rango).toFloat())
            return Offset(x, y)
        }

        // Línea guía inferior
        drawLine(
            color = colorGuia,
            start = Offset(margen, size.height - margen),
            end = Offset(size.width - margen, size.height - margen),
            strokeWidth = 1.dp.toPx()
        )

        val camino = Path()
        for (i in 0 until n) {
            val p = punto(i)
            if (i == 0) camino.moveTo(p.x, p.y) else camino.lineTo(p.x, p.y)
        }
        drawPath(
            path = camino,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        for (i in 0 until n - 1) {
            drawCircle(color = color, radius = 5.dp.toPx(), center = punto(i))
        }
        // Último punto: círculo hueco
        drawCircle(
            color = color,
            radius = 7.dp.toPx(),
            center = punto(n - 1),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}