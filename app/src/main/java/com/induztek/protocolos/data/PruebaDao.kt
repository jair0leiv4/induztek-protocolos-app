package com.induztek.protocolos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {

    @Insert
    suspend fun insertar(prueba: Prueba): Long

    @Query("SELECT * FROM pruebas WHERE codigoEquipo = :codigo ORDER BY fechaMillis DESC")
    fun observarPorEquipo(codigo: String): Flow<List<Prueba>>
}