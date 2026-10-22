package com.induztek.protocolos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {

    @Insert
    suspend fun insertar(prueba: Prueba): Long

    @Update
    suspend fun actualizar(prueba: Prueba): Int

    @Delete
    suspend fun eliminar(prueba: Prueba): Int

    @Query("SELECT * FROM pruebas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Prueba?

    @Query("SELECT * FROM pruebas WHERE codigoEquipo = :codigo ORDER BY fechaMillis DESC")
    fun observarPorEquipo(codigo: String): Flow<List<Prueba>>
}