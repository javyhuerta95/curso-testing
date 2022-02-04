package com.javyhuerta.app.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void nombreCuenta() {
        Cuenta cuenta =new Cuenta();
        cuenta.setPersona("Javy");
        String esperado = "Javy";
        String real = cuenta.getPersona();

        assertEquals(esperado,real);

    }
}