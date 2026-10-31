package com.induztek.protocolos.util

import java.security.MessageDigest
import java.security.SecureRandom

object Seguridad {

    fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.aHex()
    }

    fun hashear(contrasena: String, sal: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((sal + contrasena).toByteArray(Charsets.UTF_8)).aHex()
    }

    fun verificar(contrasena: String, sal: String, hashEsperado: String): Boolean =
        hashear(contrasena, sal) == hashEsperado

    private fun ByteArray.aHex(): String = joinToString("") { "%02x".format(it) }
}