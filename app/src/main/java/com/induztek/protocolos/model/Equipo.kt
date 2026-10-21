package com.induztek.protocolos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Equipo(
    @PrimaryKey val codigo: String,
    val tipo: String,
    val instalacion: String
)