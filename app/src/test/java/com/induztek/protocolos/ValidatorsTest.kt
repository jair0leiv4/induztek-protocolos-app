package com.induztek.protocolos

import com.induztek.protocolos.util.Validators
import org.junit.Assert.assertEquals
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

    // ---------- Tipo e instalación ----------
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
}