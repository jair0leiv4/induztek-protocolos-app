package com.induztek.protocolos.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tipos_equipo",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class TipoEquipo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String
)
