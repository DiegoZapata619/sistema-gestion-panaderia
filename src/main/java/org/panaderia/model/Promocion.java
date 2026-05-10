package org.panaderia.model;

import org.panaderia.model.Descuentos.IEstrategiaDescuento;

import java.math.BigDecimal;

public class Promocion {
    private final String id;
    private  final String nombre;
    private final IEstrategiaDescuento estrategia;
    private boolean activo;

    public Promocion (String id, String nombre, IEstrategiaDescuento estrategia){
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("El ID no puede estar vacío");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        if (estrategia == null)
            throw new IllegalArgumentException("La estrategia no puede ser nula");
        this.id=id;
        this.nombre=nombre;
        this.estrategia=estrategia;
        this.activo=true;

    }

    public BigDecimal aplicarDescuento(Venta venta){
        if (!activo) return BigDecimal.ZERO;
        return estrategia.aplicarDescuento(venta);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcionEstrategia() {
        return estrategia.getDescripcionDescuento();
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo (boolean activo){
        this.activo=activo;
    }

    public String getParametros() {
        return estrategia.getTipo().name() + ";" +
                estrategia.getParametro1() + ";" +
                estrategia.getParametro2();
    }


}
