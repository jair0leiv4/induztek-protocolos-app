package com.induztek.protocolos.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pruebas",
    foreignKeys = [
        ForeignKey(
            entity = Equipo::class,
            parentColumns = ["codigo"],
            childColumns = ["codigoEquipo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("codigoEquipo")]
)
data class Prueba(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigoEquipo: String,
    val tension: Double,
    val aislacion: Double,
    val fechaMillis: Long,
    val fotoPath: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)