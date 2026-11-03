package com.induztek.protocolos.data

import android.content.Context
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.Roles
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.util.Seguridad
import kotlinx.coroutines.flow.Flow

class ProtocolosRepository(
    private val usuarioDao: UsuarioDao,
    private val tipoEquipoDao: TipoEquipoDao,
    private val equipoDao: EquipoDao,
    private val pruebaDao: PruebaDao
) {

    // ---------- Datos iniciales (solo si la base está vacía) ----------
    suspend fun sembrarDatosIniciales() {
        if (usuarioDao.contar() == 0) {
            usuarioDao.insertar(
                Usuario(
                    nombre = "Administrador",
                    email = "admin@induztek.cl",
                    password = Seguridad.sha256("Admin123"),
                    rol = Roles.ADMIN
                )
            )
            usuarioDao.insertar(
                Usuario(
                    nombre = "Técnico de terreno",
                    email = "tecnico@induztek.cl",
                    password = Seguridad.sha256("Tecnico123"),
                    rol = Roles.TECNICO
                )
            )
        }
        if (tipoEquipoDao.contar() == 0) {
            listOf("Transformador", "Relé de protección", "Celda de media tensión", "Cable")
                .forEach { tipoEquipoDao.insertar(TipoEquipo(nombre = it)) }
        }
    }

    // ---------- Usuarios ----------
    fun usuarios(): Flow<List<Usuario>> = usuarioDao.observarTodos()

    suspend fun autenticar(email: String, password: String): Usuario? {
        val usuario = usuarioDao.buscarPorEmail(email.trim().lowercase()) ?: return null
        return if (usuario.password == Seguridad.sha256(password)) usuario else null
    }

    suspend fun guardarUsuario(usuario: Usuario): Boolean =
        usuarioDao.insertar(usuario) != -1L

    suspend fun actualizarUsuario(usuario: Usuario): Boolean =
        usuarioDao.actualizar(usuario) > 0

    suspend fun eliminarUsuario(usuario: Usuario): Boolean =
        usuarioDao.eliminar(usuario) > 0

    suspend fun buscarUsuario(id: Long): Usuario? = usuarioDao.buscarPorId(id)

    // ---------- Tipos de equipo ----------
    fun tiposEquipo(): Flow<List<TipoEquipo>> = tipoEquipoDao.observarTodos()

    suspend fun guardarTipo(tipo: TipoEquipo): Boolean =
        tipoEquipoDao.insertar(tipo) != -1L

    suspend fun actualizarTipo(tipo: TipoEquipo): Boolean =
        tipoEquipoDao.actualizar(tipo) > 0

    suspend fun eliminarTipo(tipo: TipoEquipo): Boolean =
        tipoEquipoDao.eliminar(tipo) > 0

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
    fun pruebas(): Flow<List<Prueba>> = pruebaDao.observarTodas()

    fun pruebasDe(codigo: String): Flow<List<Prueba>> =
        pruebaDao.observarPorEquipo(codigo)

    suspend fun guardarPrueba(prueba: Prueba) {
        pruebaDao.insertar(prueba)
    }

    suspend fun actualizarPrueba(prueba: Prueba): Boolean =
        pruebaDao.actualizar(prueba) > 0

    suspend fun eliminarPrueba(prueba: Prueba): Boolean =
        pruebaDao.eliminar(prueba) > 0

    suspend fun buscarPrueba(id: Long): Prueba? =
        pruebaDao.buscarPorId(id)

    companion object {
        fun crear(context: Context): ProtocolosRepository {
            val db = AppDatabase.obtener(context)
            return ProtocolosRepository(
                db.usuarioDao(),
                db.tipoEquipoDao(),
                db.equipoDao(),
                db.pruebaDao()
            )
        }
    }
}