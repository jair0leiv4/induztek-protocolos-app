package com.induztek.protocolos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.induztek.protocolos.model.TipoEquipo
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoEquipoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(tipo: TipoEquipo): Long

    @Update
    suspend fun actualizar(tipo: TipoEquipo): Int

    @Delete
    suspend fun eliminar(tipo: TipoEquipo): Int

    @Query("SELECT * FROM tipos_equipo ORDER BY nombre")
    fun observarTodos(): Flow<List<TipoEquipo>>

    @Query("SELECT COUNT(*) FROM tipos_equipo")
    suspend fun contar(): Int
}