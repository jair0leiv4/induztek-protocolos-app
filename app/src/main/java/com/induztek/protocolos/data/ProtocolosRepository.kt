package com.induztek.protocolos.data

import android.content.Context
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.Flow

class ProtocolosRepository(
    private val equipoDao: EquipoDao,
    private val pruebaDao: PruebaDao
) {
    // ---------- Equipos ----------
    fun equipos(): Flow<List<Equipo>> = equipoDao.observarTodos()

    suspend fun guardarEquipo(equipo: Equipo): Boolean =
        equipoDao.insertar(equipo) != -1L

    suspend fun actualizarEquipo(equipo: Equipo): Boolean =
        equipoDao.actualizar(equipo) > 0

    suspend fun eliminarEquipo(equipo: Equipo): Boolean =
        equipoDao.eliminar(equipo) > 0

    suspend fun existeEquipo(codigo: String): Boolean =
        equipoDao.buscarPorCodigo(codigo) != null

    suspend fun buscarEquipo(codigo: String): Equipo? =
        equipoDao.buscarPorCodigo(codigo)

    // ---------- Pruebas ----------
    suspend fun guardarPrueba(prueba: Prueba) {
        pruebaDao.insertar(prueba)
    }

    suspend fun actualizarPrueba(prueba: Prueba): Boolean =
        pruebaDao.actualizar(prueba) > 0

    suspend fun eliminarPrueba(prueba: Prueba): Boolean =
        pruebaDao.eliminar(prueba) > 0

    suspend fun buscarPrueba(id: Long): Prueba? =
        pruebaDao.buscarPorId(id)

    fun pruebasDe(codigo: String): Flow<List<Prueba>> =
        pruebaDao.observarPorEquipo(codigo)

    companion object {
        fun crear(context: Context): ProtocolosRepository {
            val db = AppDatabase.obtener(context)
            return ProtocolosRepository(db.equipoDao(), db.pruebaDao())
        }
    }
}