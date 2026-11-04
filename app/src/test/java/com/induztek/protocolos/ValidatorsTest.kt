package com.induztek.protocolos

import com.induztek.protocolos.util.Seguridad
import com.induztek.protocolos.util.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    // ---------- Correo ----------
    @Test
    fun correoVacio_devuelveError() {
        assertEquals("El correo es obligatorio", Validators.validateEmail(""))
    }

    @Test
    fun correoSinFormato_devuelveError() {
        assertEquals("Ingresa un correo válido", Validators.validateEmail("abc"))
    }

    @Test
    fun correoValido_noDevuelveError() {
        assertNull(Validators.validateEmail("tecnico@induztek.cl"))
    }

    // ---------- Contraseña ----------
    @Test
    fun passwordVacia_devuelveError() {
        assertEquals("La contraseña es obligatoria", Validators.validatePassword(""))
    }

    @Test
    fun passwordCorta_devuelveError() {
        assertEquals("Debe tener al menos 6 caracteres", Validators.validatePassword("123"))
    }

    @Test
    fun passwordValida_noDevuelveError() {
        assertNull(Validators.validatePassword("123456"))
    }

    // ---------- Nombre y rol ----------
    @Test
    fun nombreVacio_devuelveError() {
        assertEquals("El nombre es obligatorio", Validators.validateNombre(""))
    }

    @Test
    fun nombreCorto_devuelveError() {
        assertEquals("Debe tener al menos 3 caracteres", Validators.validateNombre("ab"))
    }

    @Test
    fun nombreValido_noDevuelveError() {
        assertNull(Validators.validateNombre("Ana"))
    }

    @Test
    fun rolVacio_devuelveError() {
        assertEquals("Selecciona un rol", Validators.validateRol(""))
    }

    @Test
    fun rolValido_noDevuelveError() {
        assertNull(Validators.validateRol("ADMIN"))
    }

    // ---------- Código de equipo ----------
    @Test
    fun codigoVacio_devuelveError() {
        assertEquals("El código es obligatorio", Validators.validateCodigo(""))
    }

    @Test
    fun codigoSinFormato_devuelveError() {
        assertEquals("Formato inválido, por ejemplo TR-1042", Validators.validateCodigo("hola"))
    }

    @Test
    fun codigoValido_noDevuelveError() {
        assertNull(Validators.validateCodigo("TR-1042"))
    }

    // ---------- Tipo, instalación y selección de equipo ----------
    @Test
    fun tipoVacio_devuelveError() {
        assertEquals("Selecciona un tipo de equipo", Validators.validateTipo(""))
    }

    @Test
    fun instalacionCorta_devuelveError() {
        assertEquals("Debe tener al menos 3 caracteres", Validators.validateInstalacion("ab"))
    }

    @Test
    fun instalacionValida_noDevuelveError() {
        assertNull(Validators.validateInstalacion("Sala 2"))
    }

    @Test
    fun equipoSinSeleccionar_devuelveError() {
        assertEquals("Selecciona un equipo", Validators.validateEquipoSeleccion(""))
    }

    // ---------- Tensión y aislación ----------
    @Test
    fun tensionConLetra_devuelveError() {
        assertEquals("Ingresa solo números, por ejemplo 220", Validators.validateTension("22o"))
    }

    @Test
    fun tensionCero_devuelveError() {
        assertEquals("Debe ser mayor que 0", Validators.validateTension("0"))
    }

    @Test
    fun tensionConComa_seAcepta() {
        assertNull(Validators.validateTension("220,5"))
    }

    @Test
    fun aislacionConLetras_devuelveError() {
        assertEquals("Ingresa solo números, por ejemplo 1250", Validators.validateAislacion("abc"))
    }

    @Test
    fun aislacionValida_noDevuelveError() {
        assertNull(Validators.validateAislacion("1250"))
    }

    // ---------- Fecha y hora ----------
    @Test
    fun fechaConFormatoMalo_devuelveError() {
        assertEquals(
            "Formato inválido, por ejemplo 15-10-2026 10:42",
            Validators.validateFechaHora("15/10/2026")
        )
    }

    @Test
    fun fechaQueNoExiste_devuelveError() {
        assertEquals("La fecha u hora no existe", Validators.validateFechaHora("31-02-2026 10:00"))
    }

    @Test
    fun fechaValida_noDevuelveError() {
        assertNull(Validators.validateFechaHora("15-10-2026 10:42"))
    }

    // ---------- Seguridad: la contraseña se guarda como huella SHA-256 ----------
    @Test
    fun sha256_calculaLaHuellaCorrecta() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            Seguridad.sha256("abc")
        )
    }

    @Test
    fun sha256_distintasContrasenasDanDistintasHuellas() {
        assertNotEquals(Seguridad.sha256("Admin123"), Seguridad.sha256("Admin124"))
    }
}
