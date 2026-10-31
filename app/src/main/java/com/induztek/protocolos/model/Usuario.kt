package com.induztek.protocolos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val nombreUsuario: String,
    val contrasenaHash: String,
    val sal: String,
    val rol: String
) {
    fun obtenerRol(): Rol = Rol.desde(rol)
}