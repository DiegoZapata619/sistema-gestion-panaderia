package org.panaderia.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Venta {
    private final String idVenta;
    private final Date fecha;
    private final Empleado empleado;
    private final Cliente cliente;
    private final MetodoPago metodoPago;
    /// Por buena práctica, define detalles como tipo List, una interfaz común que define métodos para
    /// todos los tipos de lista -> se depende de una abstracción
    private final List<DetalleVenta> detalles;
    private BigDecimal descuentoAplicado;

    public Venta(String idVenta, Cliente cliente, Empleado empleado, MetodoPago metodoPago) {

       /// Cada venta necesita de manera obligatoria una ID, un cliente, un empleado asociado y un metodo de pago

        if (idVenta == null || idVenta.isBlank())
            throw new IllegalArgumentException("El ID de venta no puede estar vacío");
        if (cliente == null)
            throw new IllegalArgumentException("La venta debe tener un cliente asociado");
        if (empleado == null)
            throw new IllegalArgumentException("La venta debe tener un empleado asociado");
        if (metodoPago == null)
            throw new IllegalArgumentException("Debe especificarse un método de pago");

        this.idVenta = idVenta;
        this.fecha = new Date();
        this.cliente = cliente;
        this.empleado = empleado;
        this.metodoPago = metodoPago;
        this.detalles = new ArrayList<>();
        this.descuentoAplicado = BigDecimal.ZERO;
    }


    public void agregarDetalle (Producto producto, int cantidad){
        detalles.add(new DetalleVenta(producto,cantidad));
    }

    public String getIdVenta() {
        return idVenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }


    public BigDecimal calcularSubtotal() {
        return detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void aplicarPromociones (List<Promocion> promociones){
        descuentoAplicado = promociones.stream()
                .map(p -> p.aplicarDescuento(this))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal (){
        BigDecimal total = calcularSubtotal().subtract(descuentoAplicado);
        return total.max(BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);
    }

    /// Getter para la lista de detalles. unmodifiableList es una operación de sólo lectura para que
    /// la lista no pueda ser alterada de forma externa
    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }



}
