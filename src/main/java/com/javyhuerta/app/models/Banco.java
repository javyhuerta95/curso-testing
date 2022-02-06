package com.javyhuerta.app.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banco {

    private List<Cuenta> cuentas;
    private String name;

    public Banco() {
        this.cuentas = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void addCuenta( Cuenta cuenta){
        cuentas.add(cuenta);
        cuenta.setBanco(this);
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto){
        origen.debito(monto);
        destino.credito(monto);
    }
}
