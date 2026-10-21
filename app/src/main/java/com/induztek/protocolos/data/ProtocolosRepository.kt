package com.induztek.protocolos.data

import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.Flow

class ProtocolosRepository(
    private val equipoDao: EquipoDao,
    private val pruebaDao: PruebaDao
) {
    fun equipos(): Flow<List<Equipo>> = equipoDao.observarTodos()

    suspend fun guardarEquipo(equipo: Equipo): Boolean =
        equipoDao.insertar(equipo) != -1L

    suspend fun existeEquipo(codigo: String): Boolean =
        equipoDao.buscarPorCodigo(codigo) != null

    suspend fun guardarPrueba(prueba: Prueba) {
        pruebaDao.insertar(prueba)
    }

    fun pruebasDe(codigo: String): Flow<List<Prueba>> =
        pruebaDao.observarPorEquipo(codigo)
}