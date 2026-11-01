package com.induztek.protocolos.data

import android.content.Context
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.Rol
import com.induztek.protocolos.model.Usuario
import com.induztek.protocolos.util.Seguridad
import kotlinx.coroutines.flow.Flow

class ProtocolosRepository(
    private val equipoDao: EquipoDao,
    private val pruebaDao: PruebaDao,
    private val usuarioDao: UsuarioDao
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

    // ---------- Usuarios (CRUD) ----------
    fun usuarios(): Flow<List<Usuario>> = usuarioDao.observarTodos()

    /** Crea un usuario. Devuelve false si el correo ya existe. */
    suspend fun crearUsuario(
        nombre: String,
        correo: String,
        contrasena: String,
        rol: Rol
    ): Boolean {
        val sal = Seguridad.generarSal()
        val usuario = Usuario(
            correo = correo.trim().lowercase(),
            nombre = nombre.trim(),
            contrasenaHash = Seguridad.hashear(contrasena, sal),
            sal = sal,
            rol = rol.name
        )
        return usuarioDao.insertar(usuario) != -1L
    }

    suspend fun existeUsuario(correo: String): Boolean =
        usuarioDao.buscarPorCorreo(correo.trim().lowercase()) != null

    suspend fun buscarUsuario(correo: String): Usuario? =
        usuarioDao.buscarPorCorreo(correo.trim().lowercase())

    /**
     * Edita nombre y rol. Si nuevaContrasena viene vacía o nula, se conserva la actual.
     * El correo es la clave y no se modifica.
     */
    suspend fun actualizarUsuario(
        correo: String,
        nombre: String,
        rol: Rol,
        nuevaContrasena: String? = null
    ): Boolean {
        val actual = usuarioDao.buscarPorCorreo(correo.trim().lowercase()) ?: return false
        val modificado = if (nuevaContrasena.isNullOrBlank()) {
            actual.copy(nombre = nombre.trim(), rol = rol.name)
        } else {
            val sal = Seguridad.generarSal()
            actual.copy(
                nombre = nombre.trim(),
                rol = rol.name,
                sal = sal,
                contrasenaHash = Seguridad.hashear(nuevaContrasena, sal)
            )
        }
        return usuarioDao.actualizar(modificado) > 0
    }

    suspend fun eliminarUsuario(correo: String): Boolean {
        val usuario = usuarioDao.buscarPorCorreo(correo.trim().lowercase()) ?: return false
        return usuarioDao.eliminar(usuario) > 0
    }

    // ---------- Login ----------
    /** Devuelve el usuario si correo y contraseña son correctos, o null si no. */
    suspend fun autenticar(correo: String, contrasena: String): Usuario? {
        val usuario = usuarioDao.buscarPorCorreo(correo.trim().lowercase()) ?: return null
        val correcto = Seguridad.verificar(contrasena, usuario.sal, usuario.contrasenaHash)
        return if (correcto) usuario else null
    }

    /** Si no hay usuarios, crea un admin y un técnico de demostración. */
    suspend fun asegurarUsuariosIniciales() {
        if (usuarioDao.contar() == 0) {
            crearUsuario("Administrador", "admin@duoc.cl", "Admin123", Rol.ADMIN)
            crearUsuario("Técnico", "tecnico@duoc.cl", "Tecnico123", Rol.TECNICO)
        }
    }

    companion object {
        fun crear(context: Context): ProtocolosRepository {
            val db = AppDatabase.obtener(context)
            return ProtocolosRepository(db.equipoDao(), db.pruebaDao(), db.usuarioDao())
        }
    }
}