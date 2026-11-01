package com.induztek.protocolos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.induztek.protocolos.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario): Int

    @Delete
    suspend fun eliminar(usuario: Usuario): Int

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun observarTodos(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE correo = :correo")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}