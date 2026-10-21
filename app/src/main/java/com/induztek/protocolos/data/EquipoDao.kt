package com.induztek.protocolos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.induztek.protocolos.model.Equipo
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(equipo: Equipo): Long

    @Query("SELECT * FROM equipos ORDER BY codigo")
    fun observarTodos(): Flow<List<Equipo>>

    @Query("SELECT * FROM equipos WHERE codigo = :codigo")
    suspend fun buscarPorCodigo(codigo: String): Equipo?
}