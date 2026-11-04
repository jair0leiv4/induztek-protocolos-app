package com.induztek.protocolos.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.induztek.protocolos.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore: archivo de preferencias llamado "sesion", en el almacenamiento privado de la app
private val Context.sesionDataStore by preferencesDataStore(name = "sesion")

// Lo que recordamos de quien inició sesión
data class Sesion(
    val email: String,
    val nombre: String,
    val rol: String
)

class SesionManager(context: Context) {

    private val dataStore = context.applicationContext.sesionDataStore

    private val claveEmail = stringPreferencesKey("email")
    private val claveNombre = stringPreferencesKey("nombre")
    private val claveRol = stringPreferencesKey("rol")

    // Se actualiza sola: entrega la sesión actual, o null si nadie ha iniciado sesión
    val sesion: Flow<Sesion?> = dataStore.data.map { prefs ->
        val email = prefs[claveEmail]
        val rol = prefs[claveRol]
        if (email.isNullOrBlank() || rol.isNullOrBlank()) {
            null
        } else {
            Sesion(email = email, nombre = prefs[claveNombre] ?: "", rol = rol)
        }
    }

    suspend fun guardar(usuario: Usuario) {
        dataStore.edit { prefs ->
            prefs[claveEmail] = usuario.email
            prefs[claveNombre] = usuario.nombre
            prefs[claveRol] = usuario.rol
        }
    }

    suspend fun cerrar() {
        dataStore.edit { it.clear() }
    }
}
